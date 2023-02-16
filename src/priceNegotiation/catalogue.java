package priceNegotiation;

public class catalogue {
	private Integer productPrice;
    private Integer productQuantity;


    public catalogue(Integer productPrice, Integer productQuantity) {
    	this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        //this.deliveryTime = deliveryTime;
    }
    
    public Integer getProductPrice() {
        return productPrice;
    }
    public Integer getProductQuantity() {
    	return productQuantity;
    }
    //public Integer getDeliveryTime() {
    	//return deliveryTime;
    //}
    // Getters and setters for the fields
}
