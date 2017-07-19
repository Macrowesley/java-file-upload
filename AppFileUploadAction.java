package com.chlantech.cms.action;

import com.chlantech.utils.SequenceUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


/**
 * Created by Administrator on 2017/7/12.
 */
@RestController
@RequestMapping("AppFileUploadAction")
public class AppFileUploadAction  {

    @RequestMapping(value="/fileUpload.do",method= RequestMethod.POST)
    public Map<String,Object> doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String webBase = req.getSession().getServletContext().getRealPath("/");  //获取项目根目录
        InputStream input = null;
        String fileOrinName = null;
        DiskFileItemFactory factory = new DiskFileItemFactory() ;
        ServletFileUpload upload = new ServletFileUpload(factory) ;
        upload.setFileSizeMax(10 * 1024 * 1024) ;    // 只能上传10M
        try {
            List<FileItem> items = upload.parseRequest(req) ; // 接收全部内容
            Iterator<FileItem> iter = items.iterator() ;
            while(iter.hasNext()) {
                FileItem item = iter.next();
                String fieldName = item.getFieldName();    // 取得表单控件的名称
                if(!item.isFormField()) {        // 不是普通文本
                    input = item.getInputStream() ;
                    fileOrinName = item.getName();
                    System.out.println("fileName："+item.getName());
                    break;
                }else{
                    System.out.println(item.getString());//Just print
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
            resultMap.put("flag","N");
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("flag","N");
        }
        String px = "";
        if (StringUtils.isNotBlank(fileOrinName)){
            px = fileOrinName.substring(fileOrinName.lastIndexOf("."));
        }

        DateFormatUtils.format(new Date(),"yyyyMMdd");
        Date now = new Date();

        String imgURL = DateFormatUtils.format(now,"yyyyMMdd") + "/"+ now.getTime()+ SequenceUtil.getRandomStringByLength(6)+px;

        File file =new File(webBase + "/static/upload/image/"+DateFormatUtils.format(now,"yyyyMMdd"));

        if  (!file .exists()  && !file .isDirectory())
        {
            System.out.println("//不存在");
            file .mkdir();
        } else
        {
            System.out.println("//目录存在");
        }

        webBase = webBase + "/static/upload/image/" + imgURL;

        try {
            //获取输出流
            OutputStream os = new FileOutputStream(webBase);
            //获取输入流 CommonsMultipartFile 中可以直接得到文件的流
            InputStream is = input;
            int temp;
            //一个一个字节的读取并写入
            while((temp=is.read())!=(-1))
            {
                os.write(temp);
            }
            os.flush();
            os.close();
            is.close();
            resultMap.put("flag","Y");
            resultMap.put("data",imgURL);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            resultMap.put("flag","N");
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("flag","N");
        }
        return resultMap;
    }

}
