package priceNegotiation;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.HashMap;
import jade.core.Runtime;
import java.util.Map;

public class Main extends Agent{
  public static void main(String[] args) {
    // Initialisation du conteneur JADE
    Profile profile = new ProfileImpl();
    
    profile.setParameter(Profile.MAIN_HOST, "localhost");
	
  
  }
}
