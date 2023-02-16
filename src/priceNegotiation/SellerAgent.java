package priceNegotiation;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import java.util.HashMap;


public class SellerAgent extends Agent {
    
	private HashMap<String, catalogue> products;
	private catalogue product;
	private String productTitle;
	private int productPrice;
	private int productQuantityOne;
	private int senarioType;
	
	
	protected void setup() {
		
		Object[] agentArgs = getArguments();
		
		productTitle = (String) agentArgs[0];
		productPrice = Integer.parseInt((String) agentArgs[1]);
		productQuantityOne = Integer.parseInt((String) agentArgs[2]);
		System.out.println("For the type we have 3 types, please enter a value between [1-4]");
		senarioType = Integer.parseInt((String) agentArgs[3]);
		
		
        products = new HashMap<String, catalogue>();
        product = new catalogue(productPrice,productQuantityOne);
        products.put(productTitle, product);
        
        System.out.println(products.get(productTitle));
        
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("product-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequests());
		
		addBehaviour(new PurchaseOrdersServer());

		
	}
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName().split("@")[0]+" terminating.");
	}
	
	private class OfferRequests extends CyclicBehaviour {	

		public void action() {
			
			MessageTemplate messageCFP = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			
			ACLMessage message = receive(messageCFP);
			
			
			if (message != null) {
					
					String content = message.getContent();
					String[] parts = content.split(",");

					String productName = parts[0];
					String productQuality = parts[1];
					int productQuantity = Integer.parseInt(parts[2]);
					int deliveryTime = Integer.parseInt(parts[3]);
					
					System.out.println(productName+' '+productQuality+' '+productQuantity+' '+deliveryTime);
					
					
					product = products.get(productName);
					
					if (productQuantityOne > 0 && productPrice != 0) {
						
						switch(senarioType) {
						
						case 1:
							
							if( productQuantity > 20){
								
								int discountPrice = Discount20(productPrice,deliveryTime,productQuantity);
								int price = priceWithoutDiscount(productPrice, deliveryTime, productQuantity) ;
								
								
								
								ACLMessage senarioOneReply = message.createReply();
								System.out.println(getAID().getName().split("@")[0]+": yes we have th product :"+productName+" with the price : "+price+" .and because you want a important quantity, we offre you a discount of 10%. so the final price will be :  "+discountPrice);
								senarioOneReply.setPerformative(ACLMessage.PROPOSE);
								senarioOneReply.setContent(String.valueOf(discountPrice));	
								
								send(senarioOneReply);
								
							} 
							else {
								
								
								int price = priceWithoutDiscount(productPrice, deliveryTime, productQuantity);
								
								ACLMessage normalrReply = message.createReply();
								normalrReply.setPerformative(ACLMessage.PROPOSE);
								normalrReply.setContent(String.valueOf(price));
								System.out.println(getAID().getName().split("@")[0]+": hello, the total price of the product "+productName+" is : "+price);
								
									send(normalrReply);
							}
							break;
							
						case 2:
							
							if(deliveryTime < 48) {
							   
								int discountPrice = Discount5(productPrice,productQuantity);
								int price = priceCalculation(productPrice, productQuantity);
							    
							   
							   
							   
							    ACLMessage senarioTwoReply = message.createReply();
								System.out.println(getAID().getName().split("@")[0]+": yes we have th product "+productName+" with total price :"+price+ ".But, we can't delivery to you the product before 48h. So, we offer you a discount of %5. the new price will be : "+discountPrice);
								senarioTwoReply.setPerformative(ACLMessage.PROPOSE);
								senarioTwoReply.setContent(String.valueOf(discountPrice));	
								
								send(senarioTwoReply);
							   
							}
							
							else {
								
								int price = priceCalculation(productPrice, productQuantity);
								
							    ACLMessage senarioTwoReply = message.createReply();
								System.out.println(getAID().getName().split("@")[0]+": yes we have th product :"+productName+" :"+price);
								senarioTwoReply.setPerformative(ACLMessage.PROPOSE);
								senarioTwoReply.setContent(String.valueOf(price));	
								
								send(senarioTwoReply);
							}
							
							break;
							
						case 3:
							
	                       if(productQuantity > 100) {
	                    	   
	                    	   
	                    	   if (productQuality.equals("high")) {
	                    		   
	                               int price = priceWithoutDiscount(productPrice, deliveryTime, productQuantity);
	                        	   int discountPrice = Discount30(productPrice, deliveryTime, productQuantity);
	                        	   
	                        	    ACLMessage senarioThreeReply = message.createReply();
	    							System.out.println(getAID().getName().split("@")[0]+": yes we have th product "+productName+" so the total price will be: "+price+".But unfortunately we have just the low Quality , if u want we can offer you a discount of 30%. the new price will be : "+discountPrice);
	    							senarioThreeReply.setPerformative(ACLMessage.PROPOSE);
	    							senarioThreeReply.setContent(String.valueOf(discountPrice));	
	    							
	    							send(senarioThreeReply);
	                    	   }
	                    	   else {
	                    		   
	                    		   int price = priceWithoutDiscount(productPrice, deliveryTime, productQuantity);
	                        	  
	                        	    ACLMessage senarioThreeReply = message.createReply();
	    							System.out.println(getAID().getName().split("@")[0]+": yes we have th product :"+productName+" the total price will be :"+price);
	    							senarioThreeReply.setPerformative(ACLMessage.PROPOSE);
	    							senarioThreeReply.setContent(String.valueOf(price));	
	    							
	    							send(senarioThreeReply);
	                    	   }
  
	                       }
	                       else {
	                    	   
	                    	    ACLMessage erreurReplySenarioThree = message.createReply();
	                    	    erreurReplySenarioThree.setPerformative(ACLMessage.REFUSE);
	                    	    erreurReplySenarioThree.setContent("Sorry but we dont buy less than 100 in Quantity");
		       					System.out.println("Seller: Sorry but we dont buy less than 100 in Quantity");
		       					myAgent.send(erreurReplySenarioThree);
	                       }
							
	                       break;
						
						}
						
					}
					else {
						
						ACLMessage erreurReply = message.createReply();
						erreurReply.setPerformative(ACLMessage.REFUSE);
						erreurReply.setContent("Stock Empty");
						myAgent.send(erreurReply);
					}
					
				
				
						
			  }
		      else {
				block();
			  }
				
		}
				
				
				
				
	}
	
	private class PurchaseOrdersServer extends CyclicBehaviour {
			
			public void action() {
				
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
				
				ACLMessage msg = receive(mt);
				
				if(msg != null) {
					
					String productName = msg.getContent();
					ACLMessage reply = msg.createReply();
					System.out.println("productName inside purchase:"+productName);
					
					catalogue product = products.get(productName);
					
					if (product.getProductPrice() != null) {
						reply.setPerformative(ACLMessage.INFORM);
						System.out.println(productName+" sold to agent "+msg.getSender().getName());
					}
					else {
						// The requested book has been sold to another buyer in the meanwhile .
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("not-available");
					}
					products.remove(productName);
					myAgent.send(reply);
				}
				else {
					block();
				}
				
			}
		}
		
	//Functions Of Senarios 
	public int priceCalculation(int price, int Quantity) {
		int pricecalculated = price * Quantity;
		return pricecalculated;
	}
	public int Discount20 (int price, int deliveryTime, int Quantity) {
		   // discount of 10%
		   // Add the cash for express delivrey
		
		   if(deliveryTime == 24) {
			   
			   int discountAmount = ((int)price - ((int)price * 20) / 100);
			   int discountPrice = discountAmount * Quantity ;
			   discountPrice = discountPrice + 300; 
			   return discountPrice;
			   
		   }
		   else {
			   int discountAmount = (price - (price * 20) / 100);
			   int discountPrice = discountAmount * Quantity ;
		       return discountPrice;
		   }
		   
	       
	}
	public int Discount5 (int price, int Quantity) {

					
		       int discountAmount = ((int)price - (int)((price * 10) / 100));
			   discountAmount = (int) (discountAmount * Quantity);
			   int discountPrice = discountAmount ;
			   return discountPrice;
			   
		   
		   
	       
	}
	public int priceWithoutDiscount (int price, int deliveryTime, int Quantity) {
		   // discount of 10%
		   // Add the cash for express delivrey 
		   if(deliveryTime == 24) {
			   int discountPrice = price* Quantity ;
			   discountPrice = discountPrice + 300; 
			   return discountPrice;
			   
		   }
		   else {
			   int discountPrice = price* Quantity ;
		       return discountPrice;
		   }
		   
	}
	public int Discount30 (int price, int deliveryTime, int Quantity) {
			   // discount of 10%
			   // Add the cash for express delivrey
			
			   if(deliveryTime == 24) {
				   
				   int discountAmount = ((int)price - (int)((price * 30) / 100));
				   int discountPrice = discountAmount * Quantity ;
				   discountPrice = discountPrice + 300; 
				   return discountPrice;
				   
			   }
			   else {
				   int discountAmount = ((int)price - (int)((price * 30) / 100));
				   int discountPrice = discountAmount * Quantity ;
			       return discountPrice;
			   }
			   
		       
		}
		

		
	}
	
   
	

