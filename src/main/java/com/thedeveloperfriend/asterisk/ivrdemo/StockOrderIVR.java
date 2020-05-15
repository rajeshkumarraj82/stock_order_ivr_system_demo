import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StockOrderIVR extends BaseAgiScript {
	Logger logger = LogManager.getRootLogger();
	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		
		answer();
		this.logger.debug("CALL ANSWERED ......");
		

		hangup();
		this.logger.debug("CALL HANGUP ......");
	}
}
