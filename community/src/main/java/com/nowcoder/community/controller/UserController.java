package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${quniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 生成上传文件的名称
//        String fileName = CommunityUtil.generateUUID();
//        // 设置响应信息
//        StringMap putPolicy = new StringMap();
//        putPolicy.put("returnBody", CommunityUtil.getJSONString(0));
//        // 生成七牛云的上传凭证
//        Auth auth = Auth.create(accessKey, secretKey);
//        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, putPolicy);
//
//        model.addAttribute("uploadToken", uploadToken);
//        model.addAttribute("fileName", fileName);

        return "site/setting";
    }

//    更换头像路径
//    @RequestMapping(path="/header/url", method = RequestMethod.POST)
//    @ResponseBody
//    public String updateHeaderUrl(String fileName) {
//        if (StringUtils.isBlank(fileName)) {
//            return CommunityUtil.getJSONString(1, "文件名不能为空");
//        }
//        String url = headerBucketUrl + "/" + fileName;
//        userService.updateHeader(hostHolder.getUsers().getId(), url);
//
//        return CommunityUtil.getJSONString(0);
//    }

    //    废弃
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                //  java 语言，outpustream是可以自己关闭，但是input需要自己手动关闭 finally
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

//    个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
//        用户
        model.addAttribute("user", user);
//        点赞
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

//        关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
//        粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

//        是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUsers() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUsers().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

    @RequestMapping(path = "/myreply/{userId}", method = RequestMethod.GET)
    public String getReplyPage(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
//        用户
        model.addAttribute("user", user);

//        分页
        page.setRows(commentService.findUserCommentsCount(userId));
        page.setPath("/user/myreply/" + userId);

//       回复列表
        List<Comment> commentList = commentService.findUserComments(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if( commentList != null){
            for(Comment comment: commentList){
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);
//                因为我们需要从我们的回复中点击进入原帖子，所以我们需要去存入post
                DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
                map.put("post", post);

                commentVoList.add(map);
            }
        }
        model.addAttribute("commentVoList", commentVoList);

        return "/site/my-reply";
    }

    @RequestMapping(path = "/mypost/{userId}", method = RequestMethod.GET)
    public String getPostPage(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
//        用户
        model.addAttribute("user", user);

//        分页
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/mypost/" + userId);

//       回复列表
        List<DiscussPost> postList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> postVoList = new ArrayList<>();
        if( postList != null){
            for(DiscussPost post: postList){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                long like = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("like", like);
                postVoList.add(map);
            }
        }
        model.addAttribute("postVoList", postVoList);

        return "/site/my-post";
    }
}
