package priceNegotiation;

import javax.swing.JOptionPane;

import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class BuyerAgent extends Agent {
	
	private String productName;
	private String productQuality;
	private int productQuantity;
	private int deliveryTime;
	
	private AID[] sellerAgents;
	private AID[] bestSellerAgents;
	
	
	protected void setup() {
		
		Object[] agentArgs = getArguments();
		
		if (agentArgs != null && agentArgs.length > 0) {
			
			productName = (String) agentArgs[0];
			productQuality = (String) agentArgs[1];
			productQuantity = Integer.parseInt((String) agentArgs[2]);
			deliveryTime = Integer.parseInt((String) agentArgs[3]);
	
			
			addBehaviour(new TickerBehaviour(this, 15000) {
				
				protected void onTick() {
					
					System.out.println("Trying to buy "+productQuantity+" of "+productName+" with "+productQuality+" Quality. the delivery time needed:"+deliveryTime);
					
					DFAgentDescription BuyerAgentDescription = new DFAgentDescription();
					ServiceDescription BuyerAgentService = new ServiceDescription();
					BuyerAgentService.setType("product-selling");
					BuyerAgentDescription.addServices(BuyerAgentService);
					
					try {
						
						DFAgentDescription[] resultOfSellerAgents = DFService.search(myAgent, BuyerAgentDescription); 
						System.out.println("Found "+resultOfSellerAgents.length+" seller agents.");
						sellerAgents = new AID[resultOfSellerAgents.length];
						bestSellerAgents = new AID[3];

						for (int i = 0; i < resultOfSellerAgents.length; ++i) {
							sellerAgents[i] = resultOfSellerAgents[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					addBehaviour(new RequestPerformer());
				}
			} );
		}
		else {
			System.out.println("This Product doesn't exist");
			doDelete();
			
		}
	}
	
	protected void takeDown() {
		System.out.println("BuyerAgent : "+getAID().getName()+" is termining");
	}
	
	private class RequestPerformer extends Behaviour {
		
		private int step = 0;
		private int repliesCnt = 0; 
		private AID bestSeller; // The agent who provides the best offer 
		private int bestPrice = 0; 
		private MessageTemplate messageTemplate0;
		
		
		public void action() {
			
			switch(step) {
			
			
			case 0:
				
				ACLMessage proposeOne = new ACLMessage(ACLMessage.CFP);
				
				for(int i=0; i<sellerAgents.length; i++) {
					proposeOne.addReceiver(sellerAgents[i]);
				}
				
				proposeOne.setContent(productName + "," + productQuality + "," + productQuantity + "," + deliveryTime);
				proposeOne.setReplyWith("proposeOne"+System.currentTimeMillis()); 
				proposeOne.setConversationId("product-trading");
				
				System.out.println("Buyer: I want to Buy this product "+productName);
				myAgent.send(proposeOne);
				messageTemplate0 = MessageTemplate.and(MessageTemplate.MatchConversationId("product-trading"), MessageTemplate.MatchInReplyTo(proposeOne.getReplyWith()));
				
				step = 1;
				break;
				
			case 1:
				
				ACLMessage reply = receive(messageTemplate0);
				
				if (reply != null) {
					
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						
						int price = Integer.parseInt(reply.getContent());
						
						System.out.println("Price: "+price);
							
							if ( bestSeller == null || price < bestPrice) {
								
								bestPrice = price;
								bestSeller = reply.getSender();
					
							}
							
							System.out.println("The Seller Propose to me the total  price: "+price+" for the quantity "+productQuantity+"of the product "+productName);
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						step = 2; 
					}
						
				}
				else {
					block();
				}
				break;
				
			case 2:
				
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				
				order.addReceiver(bestSeller);
				System.out.println("bestSeller: "+bestSeller);
				order.setContent(productName);
				
				order.setConversationId("product-trading");
				order.setReplyWith("order"+System.currentTimeMillis());
				
				System.out.print("Ordeeeeeer");
				
				send(order);
				messageTemplate0 = MessageTemplate.and(MessageTemplate.MatchConversationId("product-trading"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
				
			case 3:
				
				reply = receive(messageTemplate0);
				
				
				if (reply != null) {
					System.out.println("inside Buyer Purchase");
				   if (reply.getPerformative() == ACLMessage.INFORM) {
					   
						System.out.println(productName+" successfully purchased from agent "+reply.getSender().getName());
						JOptionPane.showMessageDialog(null, "successfully purchased from agent : "+reply.getSender().getName().split("@")[0]+" with price of "+bestPrice+" for "+productQuantity+" Quantity." );
						
						doDelete();
				   }
				   else {
					   System.out.println("Attempt failed: requested product already sold.");
				   }
				   
				   step = 4;
				}
				else {
					block();
				}
				break;
			
				
			
			}
		}
        public boolean done() {
			
			if (step == 4 ) {
				System.out.println("Échec");
			}
			return ((step == 4 ));
		}
		
	}

}
