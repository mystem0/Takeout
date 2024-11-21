package com.sias.waimai.controller;

import com.sias.waimai.common.CustomException;
import com.sias.waimai.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 *
 * @author li
 * @since 2023-05-12
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${waimai.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info("获取到上传的文件：{}", file.toString());
        String originalFilename = file.getOriginalFilename();//获取原始文件名。使用原始文件名遇见相同名字的文件会被覆盖
        if (originalFilename != null) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//从“.”开始截取文件名
            String fileName = UUID.randomUUID().toString() + suffix;//动态拼接文件名：自动生成的UID + 后缀名
            File dir = new File(basePath);//创建一个目录，判断当前目录是否存在。不存在--->创建当前目录
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.transferTo(new File(basePath + fileName));//将图片从临时位置转存到指定路径
            } catch (IOException e) {
                throw new CustomException(e.getMessage());
            }
            return R.success(fileName);
        }
        return R.error("上传失败");
    }

    /**
     * 文件下载
     *
     * @param httpServletResponse
     * @param name
     */
    @GetMapping("/download")
    public void download(HttpServletResponse httpServletResponse, String name) {

        try {
            //通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //通过输出流将文件写回浏览器
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            httpServletResponse.setContentType("image/jpeg");//设置返回类型
            int a;
            byte[] bytes = new byte[1024];//创建长度为1024的数组
            while ((a = fileInputStream.read(bytes)) != -1) //将输入流读取到的内容放到这个数组中，当a=-1时表示读取完成
            {
                servletOutputStream.write(bytes, 0, a);//通过输出流向浏览器写。off的值表示数组索引byte[0],读取长度为a
                servletOutputStream.flush();//刷新
            }
            //释放资源
            servletOutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
}
