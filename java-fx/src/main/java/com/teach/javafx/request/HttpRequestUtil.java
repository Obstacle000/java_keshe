package com.teach.javafx.request;

import com.teach.javafx.AppStore;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Map;

/**
 * HttpRequestUtil 后台请求实例程序，主要实践向后台发送请求的方法
 *  static boolean isLocal 业务处理程序实现方式 false java-server实现 前端程序通过下面的方法把数据发送后台程序，后台返回前端需要的数据，true 本地方式 业务处理 在SQLiteJDBC 实现
 *  String serverUrl = "http://localhost:9090" 后台服务的机器地址和端口号
 */
public class HttpRequestUtil {
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newHttpClient();
    public static String serverUrl = "http://localhost:22222";
//    public static String serverUrl = "http://202.194.7.29:22222";

    /**
     *  应用关闭是需要做关闭处理
     */
    public static void close(){
    }

    /**
     * String login(LoginRequest request)  用户登录请求实现
     * @param request  username 登录账号 password 登录密码
     * @return  返回null 登录成功 AppStore注册登录账号信息 非空，登录错误信息
     */

    public static String login(LoginRequest request){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/auth/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .build();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("response.statusCode===="+response.statusCode());
                if (response.statusCode() == 200) {
                    JwtResponse jwt = gson.fromJson(response.body(), JwtResponse.class);
                    AppStore.setJwt(jwt); // 相当于sessionStorage
                    return null;
                } else if (response.statusCode() == 401) {
                    return "用户名或密码不存在！";
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        return "登录失败";
    }

    /**
     * DataResponse request(String url,DataRequest request) 一般数据请求业务的实现
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return DataResponse 返回后台返回数据
     */
    public static DataResponse request(String url, DataRequest request){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + url))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getToken())
                    .build();
            request.add("username",AppStore.getJwt().getUsername());
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
                if (response.statusCode() == 200) {
                    //                System.out.println(response.body());
                    return gson.fromJson(response.body(), DataResponse.class);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     *  MyTreeNode requestTreeNode(String url, DataRequest request) 获取树节点对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return MyTreeNode 返回后台返回数据
     */
    public static MyTreeNode requestTreeNode(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return gson.fromJson(response.body(), MyTreeNode.class);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MyTreeNode> requestTreeNodeList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                List<Map<String,Object>> list = gson.fromJson(response.body(),List.class);
                List<MyTreeNode> rList = new ArrayList<>();
                for (Map<String, Object> stringObjectMap : list) {
                    rList.add(new MyTreeNode(stringObjectMap));
                }
                return rList;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  List<OptionItem> requestOptionItemList(String url, DataRequest request) 获取OptionItemList对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static List<OptionItem> requestOptionItemList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                OptionItemList o = gson.fromJson(response.body(), OptionItemList.class);
                return o.getItemList();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *   List<OptionItem> getDictionaryOptionItemList(String code) 获取数据字典OptionItemList对象
     * @param code  数据字典类型吗
     * @param
     * @return List<OptionItem> 返回后台返回数据
     */
    public static  List<OptionItem> getDictionaryOptionItemList(String code) {
        DataRequest req = new DataRequest();
        req.add("code", code);
        return requestOptionItemList("/api/base/getDictionaryOptionItemList",req);
    }

    /**
     *  byte[] requestByteData(String url, DataRequest request) 获取byte[] 对象 下载数据文件等
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static byte[] requestByteData(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<byte[]>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataResponse uploadFile(String fileName,String remoteFile) 上传数据文件
     * @param fileName  本地文件名
     * @param remoteFile 远程文件路径
     * @return 上传操作信息
     */
    public static DataResponse uploadFile(String uri, String fileName, String remoteFile, MyTreeNode root) {
        try {
            Path file = Path.of(fileName);
            HttpClient client = HttpClient.newBuilder().build();

            // 构建基础参数
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(serverUrl)
                    .append(uri)
                    .append("?uploader=").append(AppStore.getJwt().getId())
                    .append("&remoteFile=").append(remoteFile)
                    .append("&fileName=").append(file.getFileName());

            // 如果 root 不为空，拼接节点参数
            if (root != null) {
                urlBuilder.append("&id=").append(root.getId())
                        .append("&value=").append(root.getValue())
                        .append("&title=").append(root.getTitle())
                        .append("&pid=").append(root.getPid());
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getToken())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), DataResponse.class);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataResponse importData(String url, String fileName, String paras) 导入数据文件
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param fileName 本地文件名
     * @param paras  上传参数
     * @return 导入结果信息
     */
    public static DataResponse importData(String url, String fileName, String paras)  {
        try {
            Path file = Path.of(fileName);
            String urlStr = serverUrl+url+"?uploader=HttpTestApp&fileName=" + file.getFileName() ;
            if(paras != null && !paras.isEmpty())
                urlStr += "&"+paras;
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return gson.fromJson(response.body(), DataResponse.class);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DataResponse importData(String url, File file, String paras) {
        try {
            HttpClient client = HttpClient.newBuilder().build();

            String urlStr = serverUrl + url + "?uploader=HttpTestApp&fileName=" + file.getName();
            if (paras != null && !paras.isEmpty()) {
                urlStr += "&" + paras;
            }

            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            String LINE_FEED = "\r\n";

            // 拼接 multipart/form-data 请求体前半部分
            StringBuilder sb = new StringBuilder();
            sb.append("--").append(boundary).append(LINE_FEED);
            sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"").append(LINE_FEED);
            sb.append("Content-Type: application/octet-stream").append(LINE_FEED);
            sb.append(LINE_FEED);

            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            byte[] beforeFile = sb.toString().getBytes();
            byte[] afterFile = (LINE_FEED + "--" + boundary + "--" + LINE_FEED).getBytes();

            // 合并请求体字节
            byte[] requestBody = new byte[beforeFile.length + fileBytes.length + afterFile.length];
            System.arraycopy(beforeFile, 0, requestBody, 0, beforeFile.length);
            System.arraycopy(fileBytes, 0, requestBody, beforeFile.length, fileBytes.length);
            System.arraycopy(afterFile, 0, requestBody, beforeFile.length + fileBytes.length, afterFile.length);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("Authorization", "Bearer " + AppStore.getJwt().getToken())
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), DataResponse.class);
            } else {
                System.err.println("上传失败，状态码：" + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
