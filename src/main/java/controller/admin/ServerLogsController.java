package controller.admin;

import datatypes.server.ServerLog;
import enums.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class ServerLogsController {
    private static final Logger logger = LogManager.getLogger(ServerLogsController.class);

    @GetMapping("/server-logs")
    public String loadPage(Model model) {
        System.out.println("asksks");
        String fileName = "application.log";
        String line;
        List<ServerLog> serverLogs = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                try{
                    String[] ar = line.split("!!");
                    Level level = Level.getByValue(ar[1]);
                    serverLogs.add(new ServerLog(level, ar[0], ar[2]));
                }catch (Exception ignored) {

                }
            }
            bufferedReader.close();
            Collections.reverse(serverLogs);
            model.addAttribute("logs",serverLogs);
        }
        catch(FileNotFoundException ex) {
            logger.error("Unable to open file '{}'",fileName);
        }
        catch(IOException ex) {
            logger.error("Error reading file '{}'", fileName);
        }

        return "server-logs";
    }
    @GetMapping("/clearLogs")
    public String clearLogs(HttpSession session) throws FileNotFoundException {
        String fileName = "application.log";
        new PrintWriter(fileName).close();
        session.setAttribute("info", "Logs are deleted successfully");
        return "server-logs";
    }

}
