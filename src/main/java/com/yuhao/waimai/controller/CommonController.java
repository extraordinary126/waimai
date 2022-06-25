package com.yuhao.waimai.controller;

import com.yuhao.waimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/** 文件的上传和下载Controller
 * */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    //读取配置文件中自定义的路径
    @Value("${project.localPath}")
    private String localPath;


    //文件上传方法
    @PostMapping("/upload")
    //Content-Disposition: form-data;( name="file"); filename="0f252364-a561-4e8d-8065-9a6797a6b1d3.jpg"
                                //参数名必须和前端保持一致 name="file" 否则为null
    public R<String> upload(MultipartFile file){
        //file是一个临时文件 需要转存到其他目录  否则在请求结束后文件被删除
        log.info(file.toString());
        //使用UUID生成随机文件名 防止文件名重复 造成文件覆盖
        String randomFileName = UUID.randomUUID().toString();

        //用原始的文件名获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        String fileName = randomFileName + suffix;

        //File既可以是一个文件 也可以是一个目录
        //判断目标目录是否存在 若不存在则创建
        File dir = new File(localPath);
        if (!dir.exists()){
            //目录不存在,创建目录
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置   路径 + 文件名 + 后缀
            file.transferTo(new File(localPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    //请求网址: http://localhost:8083/common/download?name=bfb130ee-3c85-4387-b190-c1f03490498f
    //请求方法: GET
    @GetMapping("/download")    //name是前端传过来的图片文件名
    public void download(String name, HttpServletResponse response){
        try {
            //输入流 通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(localPath + name));

            //输出流 通过输出流 将文件写到浏览器上 在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应到浏览器的是什么文件
            response.setContentType("image/jpeg");

            int length = 0;
            byte[] bytes = new byte[1024];
            //　1、此方法是从输入流中读取一个数据的字节，通俗点讲，即每调用一次read方法，从FileInputStream中读取一个字节。
            //　　2、返回下一个数据字节，如果已达到文件末尾，返回-1,
            //  一边读一边写
            while ( (length = fileInputStream.read(bytes)) != -1){
                //public void write(byte[] b,int off,int len):写一个字节数组的一部分，off表示从这个索引开始，长度为len
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
