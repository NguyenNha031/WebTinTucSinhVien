package com.nhom10.webtintuckhoacntt.controller;

import com.nhom10.webtintuckhoacntt.enums.PostStatus;
import com.nhom10.webtintuckhoacntt.model.*;
import com.nhom10.webtintuckhoacntt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserrRepository userrRepository;

    @Autowired
    private PageRepository pageRepository;
    private void injectCommonModelAttributes(Model model) {
        List<Menu> menus = menuRepository.findAll(Sort.by("id"));
        model.addAttribute("menus", menus);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : pageRepository.findAll()) {
            menuToPageMap.put(page.getIdMenu(), page.getIdPage());
        }
        model.addAttribute("menuToPageMap", menuToPageMap);
    }
    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa đăng";
        if (duration.toMinutes() < 60) return "Đã đăng " + duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Đã đăng " + duration.toHours() + " giờ trước";
        return "Đã đăng " + duration.toDays() + " ngày trước";
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        injectCommonModelAttributes(model);
        model.addAttribute("currentPage", "home");

        List<Banner> banners = bannerRepository.findAllByOrderByIdAsc();
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            bannerMap.put("image", b.getAnh());
            bannerMap.put("tooltip", b.getMota());
            bannerMap.put("link", b.getIdPost() != null ? "/post/" + b.getIdPost() : "#");
            bannerList.add(bannerMap);
        }
        model.addAttribute("bannerList", bannerList);

        List<Post> allPosts = postRepository.findPublishedPostsOrderByPined(PostStatus.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            postMap.put("image", p.getImage());
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : pageRepository.findAll()) {
            menuToPageMap.put(page.getIdMenu(), page.getIdPage());
        }
        model.addAttribute("menuToPageMap", menuToPageMap);

        return "index";
    }


    @GetMapping("/tin-tuc")
    public String showAllNews(Model model, HttpSession session) {
        injectCommonModelAttributes(model);
        model.addAttribute("currentPage", "news");

        Userr currentUser = (Userr) session.getAttribute("loggedInUser");

        List<Post> rawPosts = (currentUser != null)
                ? postRepository.findAll(Sort.by(Sort.Direction.DESC, "pined", "updatedAt"))
                : postRepository.findPublishedPostsOrderByPined(PostStatus.Published);

        List<Map<String, Object>> posts = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        for (Post p : rawPosts) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("id", p.getIdPost());
            postMap.put("title", p.getTitle());
            postMap.put("image", p.getImage());
            postMap.put("tooltip", p.getMota());
            postMap.put("content", p.getContent());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("createdAt", p.getCreatedAt().format(fmt)); // chuỗi ISO cho datetime-local
            postMap.put("updatedAt", p.getUpdatedAt().format(fmt));
            postMap.put("link", "/post/" + p.getIdPost());
            postMap.put("status", p.getStatus().name());
            postMap.put("pined", p.getPined());
            posts.add(postMap);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("pageCss", "/css/news.css");

        return "news";
    }

    @GetMapping("/post/{id}")
    public String showPostDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return "redirect:/";
        Post post = postOpt.get();
        Userr currentUser = (Userr) session.getAttribute("loggedInUser");
        if (currentUser == null && post.getStatus() != PostStatus.Published) {
            return "redirect:/";
        }
        String authorName = userrRepository.findById(post.getCreatedBy())
                .map(Userr::getFullname)
                .orElse("Không rõ");
        model.addAttribute("post", post);
        model.addAttribute("timeAgo", getTimeAgo(post.getCreatedAt()));
        model.addAttribute("contentHtml", post.getContent());
        model.addAttribute("authorName", authorName);
        return "post-detail";
    }

    @PostMapping("/addpost")
    public String addOrUpdatePost(Long idPost, String title, String mota, String image, String status,
                                  String content, String createdAt, String updatedAt,
                                  Boolean pined, HttpSession session) {

        Userr currentUser = (Userr) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/tin-tuc";

        Post post = (idPost != null)
                ? postRepository.findById(idPost).orElse(new Post())
                : new Post();

        post.setTitle(title);
        post.setMota(mota);
        post.setImage((image == null || image.isBlank())
                ? "https://link-default.jpg"
                : image);
        post.setStatus(PostStatus.valueOf(status));
        post.setCreatedBy(currentUser.getId());
        post.setContent(content);

        post.setCreatedAt((post.getCreatedAt() == null)
                ? LocalDateTime.now()
                : post.getCreatedAt());

        post.setUpdatedAt(LocalDateTime.now());

        post.setPined(pined != null && pined);

        postRepository.save(post);
        return "redirect:/tin-tuc";
    }
    @GetMapping("/page/{id}")
    public String showPageDetail(@PathVariable("id") Long id, Model model) {
        injectCommonModelAttributes(model);
        Optional<Page> pageOpt = pageRepository.findById(id);
        if (pageOpt.isEmpty()) return "redirect:/";
        Page page = pageOpt.get();
        model.addAttribute("page", page);
        model.addAttribute("currentPageId", page.getIdPage());
        model.addAttribute("contentHtml", page.getContent());
        return "page-detail";
    }


    @PostMapping("/deletepost")
    public String deletePost(Long idPost, HttpSession session) {
        Userr currentUser = (Userr) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/tin-tuc";

        postRepository.deleteById(idPost);
        return "redirect:/tin-tuc";
    }



}