/**
 * All rights reserved.
 */

package singh.mahabir.ftp.ftp.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.impl.ABOR;
import org.apache.ftpserver.command.impl.ACCT;
import org.apache.ftpserver.command.impl.APPE;
import org.apache.ftpserver.command.impl.AUTH;
import org.apache.ftpserver.command.impl.CDUP;
import org.apache.ftpserver.command.impl.DefaultCommandFactory;
import org.apache.ftpserver.command.impl.EPRT;
import org.apache.ftpserver.command.impl.EPSV;
import org.apache.ftpserver.command.impl.FEAT;
import org.apache.ftpserver.command.impl.HELP;
import org.apache.ftpserver.command.impl.LANG;
import org.apache.ftpserver.command.impl.MD5;
import org.apache.ftpserver.command.impl.MDTM;
import org.apache.ftpserver.command.impl.MFMT;
import org.apache.ftpserver.command.impl.MKD;
import org.apache.ftpserver.command.impl.MLST;
import org.apache.ftpserver.command.impl.MODE;
import org.apache.ftpserver.command.impl.NOOP;
import org.apache.ftpserver.command.impl.OPTS;
import org.apache.ftpserver.command.impl.PASV;
import org.apache.ftpserver.command.impl.PBSZ;
import org.apache.ftpserver.command.impl.PORT;
import org.apache.ftpserver.command.impl.PROT;
import org.apache.ftpserver.command.impl.PWD;
import org.apache.ftpserver.command.impl.QUIT;
import org.apache.ftpserver.command.impl.REIN;
import org.apache.ftpserver.command.impl.REST;
import org.apache.ftpserver.command.impl.RMD;
import org.apache.ftpserver.command.impl.RNFR;
import org.apache.ftpserver.command.impl.RNTO;
import org.apache.ftpserver.command.impl.SITE;
import org.apache.ftpserver.command.impl.SITE_DESCUSER;
import org.apache.ftpserver.command.impl.SITE_HELP;
import org.apache.ftpserver.command.impl.SITE_STAT;
import org.apache.ftpserver.command.impl.SITE_WHO;
import org.apache.ftpserver.command.impl.SITE_ZONE;
import org.apache.ftpserver.command.impl.SIZE;
import org.apache.ftpserver.command.impl.STOU;
import org.apache.ftpserver.command.impl.STRU;
import org.apache.ftpserver.command.impl.SYST;
import org.apache.ftpserver.command.impl.TYPE;
import org.apache.ftpserver.command.impl.USER;

import singh.mahabir.ftp.ftp.command.MyCWD;
import singh.mahabir.ftp.ftp.command.MyDELE;
import singh.mahabir.ftp.ftp.command.MyLIST;
import singh.mahabir.ftp.ftp.command.MyMLSD;
import singh.mahabir.ftp.ftp.command.MyNLST;
import singh.mahabir.ftp.ftp.command.MyPASS;
import singh.mahabir.ftp.ftp.command.MyRETR;
import singh.mahabir.ftp.ftp.command.MySTAT;
import singh.mahabir.ftp.ftp.command.MySTOR;

/**
 * @author Mahabir Singh
 *
 */
public class DBCommandFactory {

    private static final HashMap<String, Command> DEFAULT_COMMAND_MAP = new HashMap<>();

    static {
	DEFAULT_COMMAND_MAP.put("ABOR", new ABOR());
	DEFAULT_COMMAND_MAP.put("ACCT", new ACCT());
	DEFAULT_COMMAND_MAP.put("APPE", new APPE());
	DEFAULT_COMMAND_MAP.put("AUTH", new AUTH());
	DEFAULT_COMMAND_MAP.put("CDUP", new CDUP());
	DEFAULT_COMMAND_MAP.put("CWD", new MyCWD());
	DEFAULT_COMMAND_MAP.put("DELE", new MyDELE());
	DEFAULT_COMMAND_MAP.put("EPRT", new EPRT());
	DEFAULT_COMMAND_MAP.put("EPSV", new EPSV());
	DEFAULT_COMMAND_MAP.put("FEAT", new FEAT());
	DEFAULT_COMMAND_MAP.put("HELP", new HELP());
	DEFAULT_COMMAND_MAP.put("LANG", new LANG());
	DEFAULT_COMMAND_MAP.put("LIST", new MyLIST());
	DEFAULT_COMMAND_MAP.put("MD5", new MD5());
	DEFAULT_COMMAND_MAP.put("MFMT", new MFMT());
	DEFAULT_COMMAND_MAP.put("MMD5", new MD5());
	DEFAULT_COMMAND_MAP.put("MDTM", new MDTM());
	DEFAULT_COMMAND_MAP.put("MLST", new MLST());
	DEFAULT_COMMAND_MAP.put("MKD", new MKD());
	DEFAULT_COMMAND_MAP.put("MLSD", new MyMLSD());
	DEFAULT_COMMAND_MAP.put("MODE", new MODE());
	DEFAULT_COMMAND_MAP.put("NLST", new MyNLST());
	DEFAULT_COMMAND_MAP.put("NOOP", new NOOP());
	DEFAULT_COMMAND_MAP.put("OPTS", new OPTS());
	DEFAULT_COMMAND_MAP.put("PASS", new MyPASS());
	DEFAULT_COMMAND_MAP.put("PASV", new PASV());
	DEFAULT_COMMAND_MAP.put("PBSZ", new PBSZ());
	DEFAULT_COMMAND_MAP.put("PORT", new PORT());
	DEFAULT_COMMAND_MAP.put("PROT", new PROT());
	DEFAULT_COMMAND_MAP.put("PWD", new PWD());
	DEFAULT_COMMAND_MAP.put("QUIT", new QUIT());
	DEFAULT_COMMAND_MAP.put("REIN", new REIN());
	DEFAULT_COMMAND_MAP.put("REST", new REST());
	DEFAULT_COMMAND_MAP.put("RETR", new MyRETR());
	DEFAULT_COMMAND_MAP.put("RMD", new RMD());
	DEFAULT_COMMAND_MAP.put("RNFR", new RNFR());
	DEFAULT_COMMAND_MAP.put("RNTO", new RNTO());
	DEFAULT_COMMAND_MAP.put("SITE", new SITE());
	DEFAULT_COMMAND_MAP.put("SIZE", new SIZE());
	DEFAULT_COMMAND_MAP.put("SITE_DESCUSER", new SITE_DESCUSER());
	DEFAULT_COMMAND_MAP.put("SITE_HELP", new SITE_HELP());
	DEFAULT_COMMAND_MAP.put("SITE_STAT", new SITE_STAT());
	DEFAULT_COMMAND_MAP.put("SITE_WHO", new SITE_WHO());
	DEFAULT_COMMAND_MAP.put("SITE_ZONE", new SITE_ZONE());

	DEFAULT_COMMAND_MAP.put("STAT", new MySTAT());
	DEFAULT_COMMAND_MAP.put("STOR", new MySTOR());
	DEFAULT_COMMAND_MAP.put("STOU", new STOU());
	DEFAULT_COMMAND_MAP.put("STRU", new STRU());
	DEFAULT_COMMAND_MAP.put("SYST", new SYST());
	DEFAULT_COMMAND_MAP.put("TYPE", new TYPE());
	DEFAULT_COMMAND_MAP.put("USER", new USER());
    }

    private HashMap<String, Command> commandMap = new HashMap<>();

    private boolean useDefaultCommand = true;

    public CommandFactory createCommandFactory() {
	HashMap<String, Command> mergedCommands = new HashMap<>();
	if (useDefaultCommand) {
	    mergedCommands.putAll(DEFAULT_COMMAND_MAP);
	}
	mergedCommands.putAll(commandMap);
	return new DefaultCommandFactory(mergedCommands);
    }

    public boolean isUseDefaultCommand() {
	return useDefaultCommand;
    }

    public void setUseDefaultCommand(boolean useDefaultCommand) {
	this.useDefaultCommand = useDefaultCommand;
    }

    public Map<String, Command> getCommandMap() {
	return commandMap;
    }

    public void setCommandMap(Map<String, Command> commandMap) {
	if (commandMap == null) {
	    throw new NullPointerException("Command name can not be null");
	}
	this.commandMap.clear();
	for (Entry<String, Command> entry : commandMap.entrySet()) {
	    this.commandMap.put(entry.getKey().toUpperCase(), entry.getValue());
	}
    }

    public void addCommandMap(String commandName, Command command) {
	if (command == null || commandName == null) {
	    throw new NullPointerException("Command or Command name should not be null");
	}
	commandMap.put(commandName.toUpperCase(), command);
    }

}
