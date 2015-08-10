package info.yangguo.demo.script

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/7/23
 * Time:下午2:45
 * <p/>
 * Description:
 * <p/>
 * SSH2测试代码
 */
public class SSH2Session {
    private String host
    private int port
    private String user
    private String pass
    Session session

    SSH2Session(host, port = 22, user, pass) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    def openSession() {
        JSch jsch = new JSch();
        session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");

        session.setPassword(pass);
        session.connect();
    }

    def closeSession() {
        session.disconnect();
    }
}
