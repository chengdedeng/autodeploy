package info.yangguo.demo.script

import com.google.common.collect.Lists
import org.apache.commons.io.IOUtils

/**
 * Created by IntelliJ IDEA
 * User:杨果
 * Date:15/8/3
 * Time:下午1:55 
 *
 * Description:
 */
class Deploy {
    static arguments = ["PP" : "PackagePath 本地需要打包的项目路径", "PC": "PackageCommand 打包指令", "PF": "PackageFile 本地打好的包文件全路径",
                        "RF" : "RemoteFile 远程的包文件全路径", "RH": "RemoteHost 远程服务器的IP", "RPT": "RemotePort 远程服务器的端口", "RU": "RemoteUser 远程服务器的账号",
                        "RPD": "RemotePassword 远程服务器的密码", "RC": "RemoteCommand 远程服务启动脚本", "RL": "RemoteLoggerFile 远程服务的日志"]

    static SSH2Session ssh2Session;

    public static void main(String[] args) {
        GroovyClassLoader classLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
        if (null == args || args.size() == 0) {
            while (true) {
                println "请输入部署指令,如需帮助请输入help."
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                def parameter = reader.readLine()
                if ("help".equals(parameter)) {
                    for (Map.Entry entry : arguments.entrySet()) {
                        println(entry.key + ":" + entry.value)
                    }
                    println "例如:def map= [\"PC\":\"gradle clean war\",\"PP\": \"/Users/yangguo/work/code/flight-price/tops-eterm-interface/tz-eterm-interface-web\", \"PF\": \"/Users/yangguo/work/code/flight-price/tops-eterm-interface/tz-eterm-interface-web/target/libs/tz-eterm-interface-web.war\",\"RF\": \"/home/yangguo/tomcat-7.0.54/webapps/tz-eterm-interface-web.war\", \"RH\": \"*.*.*.*\", \"RPT\": 22, \"RU\": \"****\", \"RPD\": \"****\", \"RC\": \"cd /data/tomcat-eterm/bin;./shutdown.sh;ps x|grep /data/tomcat-eterm/| grep -v grep|awk \\'{print \\\$1}\\'|xargs kill -9;cd /data/tomcat-eterm/logs/;rm -rf catalina.out;cd /data/tomcat-eterm/bin;./startup.sh\", \"RL\": \"/data/log/tops/tz-eterm-interface-web(10.3.41.113:8080)-debug-current.log\"]";
                } else {
                    def groovyShell = new GroovyShell()
                    def commands = groovyShell.evaluate(parameter)
                    packaging(commands)
                    scping(commands)
                    starting(commands)
                    logging(commands)
                    break
                }
            }
        } else {
            def parameter = args[0]
            Class scriptClass = classLoader.defineClass("script", parameter)
            def field = scriptClass.getField("map")
            def scObject = scriptClass.newInstance()
            def commands = field.get(scObject)
            packaging(commands)
            scping(commands)
            starting(commands)
            logging(commands)
        }
    }

    def static packaging = { Map<String, String> commands ->
        println "打包开始"
        def pc = Lists.newArrayList(commands.get("PC").split(" "))
        def pp = commands.get("PP")
        def processBuilder = new ProcessBuilder(pc)
        processBuilder.directory(new File(pp))
        def process = processBuilder.start()
        def inputStream = process.getInputStream()
        for (line in IOUtils.readLines(inputStream))
            println(line);
        IOUtils.closeQuietly(inputStream)
        println "打包结束"
    }

    def static scping = { Map<String, String> commands ->
        println "scp开始"
        def rh = commands.get("RH")
        def rpt = commands.get("RPT")
        def ru = commands.get("RU")
        def rpd = commands.get("RPD")
        def pf = commands.get("PF")
        def rf = commands.get("RF")
        ssh2Session = new SSH2Session(rh, rpt, ru, rpd)
        ssh2Session.openSession();
        Operate.put.call(ssh2Session.session, pf, rf)
        println("scp完成")
    }

    def static starting = { Map<String, String> commands ->
        println "启动开始"
        def rc = commands.get("RC")
        Operate.cmd.call(ssh2Session.session, rc)
        println "启动结束"
    }

    def static logging = { Map<String, String> commands ->
        println "开始查看日志"
        sleep(1000 * 5)
        def rl = commands.get("RL")
        Operate.getStream.call(ssh2Session.session, rl)
        ssh2Session.closeSession();
        println "结束日志查看"
    }
}
