package playground.logic.service;

public enum ConstantsValues {
	PLAYGROUND("2019A.kariv"), 
	USERNAME("temp_username"), AVATAR("temp_avatar"), 
	VERIFICATION_CODE("whynot"), 
	PLAYER("player"), MANAGER("manager");

    private String value; 
  
    public String getValue() 
    { 
        return this.value; 
    } 
  
    private ConstantsValues(String value) 
    { 
        this.value = value; 
    } 
}
