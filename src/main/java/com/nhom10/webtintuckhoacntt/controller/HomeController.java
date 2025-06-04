package com.nhom10.webtintuckhoacntt.controller;

import com.nhom10.webtintuckhoacntt.enums.PostStatus;
import com.nhom10.webtintuckhoacntt.model.*;
import com.nhom10.webtintuckhoacntt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private BlockRepository blockRepository;

    @Autowired
    private UserrRepository userrRepository;

    @Autowired
    private PageRepository pageRepository;


    @GetMapping("/")
    public String showHomePage(Model model) {
        injectCommonModelAttributes(model);
        model.addAttribute("currentPage", "home");
        // Banners
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

        // Menus

        // Posts
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

        // ✅ Add menuToPageMap here
        List<Page> allPages = pageRepository.findAll();
        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : allPages) {
            menuToPageMap.put(page.getIdMenu(), page.getIdPage());
        }
        model.addAttribute("menuToPageMap", menuToPageMap);

        return "index";
    }

    private void injectCommonModelAttributes(Model model) {
        // Menus
        List<Menu> menus = menuRepository.findAll(Sort.by("id"));
        model.addAttribute("menus", menus);

        // Map Menu -> Page
        List<Page> allPages = pageRepository.findAll();
        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Page page : allPages) {
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

    @GetMapping("/tin-tuc")
    public String showAllNews(Model model) {
        injectCommonModelAttributes(model);
        model.addAttribute("currentPage", "news");
        List<Map<String, String>> posts = new ArrayList<>();
        for (Post p : postRepository.findPublishedPostsOrderByPined(PostStatus.Published)) {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("title", p.getTitle());
            postMap.put("image", p.getImage());
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        // ✅ THÊM DÒNG NÀY để truyền biến pageCss cho fragment/head.html
        model.addAttribute("pageCss", "/css/news.css");

        return "news"; // file: templates/news.html
    }

    @GetMapping("/post/{id}")
    public String showPostDetail(@PathVariable("id") Long id, Model model) {

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return "redirect:/";

        Post post = postOpt.get();
        if (post.getStatus() != PostStatus.Published) return "redirect:/";

        Optional<Block> blockOpt = blockRepository.findByIdPost(post.getIdPost());
        String authorName = userrRepository.findById(post.getCreatedBy())
                .map(Userr::getFullname)
                .orElse("Không rõ");

        model.addAttribute("post", post);
        model.addAttribute("timeAgo", getTimeAgo(post.getCreatedAt()));
        model.addAttribute("contentHtml", blockOpt.map(Block::getCode).orElse(""));
        model.addAttribute("authorName", authorName);

        return "post-detail";
    }

    @GetMapping("/page/{id}")
    public String showPageDetail(@PathVariable("id") Long id, Model model) {
        injectCommonModelAttributes(model);
        Optional<Page> pageOpt = pageRepository.findById(id);
        if (pageOpt.isEmpty()) return "redirect:/";

        Page page = pageOpt.get();
        model.addAttribute("page", page);
        model.addAttribute("currentPageId", page.getIdPage());

        List<Map<String, String>> blocks = new ArrayList<>();
        for (Block block : blockRepository.findByIdPage(page.getIdPage())) {
            Map<String, String> blockMap = new HashMap<>();
            blockMap.put("code", block.getCode());
            blocks.add(blockMap);
        }

        model.addAttribute("contentHtml", blocks);

        return "page-detail";
    }

}
