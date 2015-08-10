package info.yangguo.demo.script

import com.jcraft.jsch.*
import org.apache.commons.io.IOUtils;

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/7/24
 * Time:上午10:43
 * <p/>
 * Description:
 */
public class Operate {
    /**
     * 将本地文件SCP到远程
     */
    def static put = { session, fsrc, fdest ->
        ChannelSftp channel = session.openChannel("sftp");
        channel.connect();
        channel.put(fsrc, fdest);
        channel.disconnect();
    }
    /**
     * 将远程文件拷贝到本地
     */
    def static getFile = { session, fsrc, fdest ->
        ChannelSftp channel = session.openChannel("sftp");
        channel.connect();
        channel.get(fsrc, fdest);
        channel.disconnect();
    }
    /**
     * 获取远程文件流
     */
    def static getStream = { session, fsrc ->
        ChannelSftp channel = session.openChannel("sftp");
        channel.connect();
        List<String> lists = IOUtils.readLines(channel.get(fsrc));
        for (String line : lists) {
            println(line)
        }
        channel.disconnect();
    }
    /**
     * 在远程服务器执行命令
     */
    def static cmd = { session, command ->
        ChannelExec channel = session.openChannel("exec");
        channel.setCommand(command)
        channel.connect();
        List<String> lists = IOUtils.readLines(channel.getInputStream());
        for (String line : lists) {
            println(line)
        }
        channel.disconnect();
    }
}
