package com.alexa.dem;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.TableDescription;



/**
 * This project shows how to create a Lambda function for handling Alexa Skill requests that:
 * Can be used to store a player's runs
 * fetch the player's runs
**/
public class CricketSpeechlet implements Speechlet {
	
	public CricketSpeechlet(){
		

	}
	
    private static final Logger log = LoggerFactory.getLogger(CricketSpeechlet.class);

    private AmazonDynamoDBClient amazonDynamoDBClient;

    private static final String SLOT_PLAYER_NAME = "PlayerName";
    private static final String SLOT_RUNS = "Runs";


    public static final String COMPLETE_HELP =
            "Here's some things you can say. add John has scored 112 runs, "
                    + "get runs scored by John";

    

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session) throws SpeechletException{
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // initialization area
        try {
			initializeComponents();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request,final Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
       

        Intent intent = request.getIntent();
        if ("NewEntryIntent".equals(intent.getName())) {
            return addNewItemIntentResponse(session, intent);

        } else if ("GetEntryIntent".equals(intent.getName())) {
            return getSumItemIntentResponse(session, intent);

        } else if ("AMAZON.HelpIntent".equals(intent.getName())) {
            return getHelpIntentResponse(session, intent);
            
        } else if ("AMAZON.StopIntent".equals(intent.getName())) {
                return exitIntentResponse(session, intent);

        } else if ("AMAZON.CancelIntent".equals(intent.getName())) {
            return exitIntentResponse(session, intent);

        } else if ("ResetIntent".equals(intent.getName())) {
            return getResetIntentResponse(session, intent);

        } else {
            throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        	// any cleanup logic goes here
        	
        
    }

    /**
     * Initializes the instance components if needed.
     * @throws InterruptedException 
     */
    private void initializeComponents() throws InterruptedException {
    	
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        
        CricketManagerHelpers.createCricketManagerTable("CricketManagerTable",client);
    }
    
    private SpeechletResponse getWelcomeResponse() {
		String speechText = "Welcome to the Player data storage system. In this system, you can add the game details of a cricketer in any cricket format. "+
				"You can say"+
				"add John has scored 112 runs, "+
				"get runs scored by John, "+
				"and so on and so forth.";

		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Welcome");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create reprompt
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(speech);

		return SpeechletResponse.newAskResponse(speech,reprompt,card);
	}
    
    
    public SpeechletResponse addNewEntryIntentResponse(Session session,Intent intent) {
        // add an entry
       try{
        String newPlayer =
                intent.getSlot(SLOT_PLAYER_NAME).getValue();
        String newRuns =
                intent.getSlot(SLOT_RUNS).getValue();
       
      
        String speechText = "Player details have been added";
        
        
        
        		
        
          		if (newPlayer == null) {
          			speechText="Sorry, that was an invalid player name.";	
          			SimpleCard card = new SimpleCard();
    				card.setTitle("Error");
    				card.setContent(speechText);

    		 		// Create the plain text output.
    		 		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    		 		speech.setText(speechText);

    		 		// Create reprompt
    		 		Reprompt reprompt = new Reprompt();
    		 		reprompt.setOutputSpeech(speech);
    		 		
    		 		return SpeechletResponse.newTellResponse(speech, card);
          		//	return CricketManagerHelpers.getCricketTellSpeechletResponse(speechText);
                }
          		if (newRuns == null) {
          			speechText="Sorry, that is an invalid runs data.";	
          			SimpleCard card = new SimpleCard();
    				card.setTitle("Error");
    				card.setContent(speechText);

    		 		// Create the plain text output.
    		 		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    		 		speech.setText(speechText);

    		 		// Create reprompt
    		 		Reprompt reprompt = new Reprompt();
    		 		reprompt.setOutputSpeech(speech);
    		 		
    		 		return SpeechletResponse.newTellResponse(speech, card);
          		//	return CricketManagerHelpers.getCricketTellSpeechletResponse(speechText);
                }
          		 
				// Create the Simple card content.
      			SimpleCard card = new SimpleCard();
      			card.setTitle("Add new");
      			card.setContent(speechText);
             	

          		// Create the plain text output.
          		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
          		speech.setText(speechText);

          		// Create reprompt
          		Reprompt reprompt = new Reprompt();
          		reprompt.setOutputSpeech(speech);
        
        
        
        //logic for adding to DynamoDB
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("CricketManagerTable");
       
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        Item item=new Item().withPrimaryKey("DataPlayer",newPlayer).withString("DataRuns",newRuns);
        table.putItem(item);
        
         
        
     

     		return SpeechletResponse.newTellResponse(speech,card);
       }catch(NumberFormatException e){
    	   String speechText="Sorry, that was an invalid entry.";	
 			return CricketManagerHelpers.getCricketTellSpeechletResponse(speechText);
       }

        
    }

    
    public SpeechletResponse getEntryResponse(Session session,Intent intent) {
        try{
    	String getPlayer =
                intent.getSlot(SLOT_PLAYER_NAME).getValue();

    	
    	
    	String speechText="Get runs";
    	
    				
    	
    	if (getPlayer == null) {
  			speechText="Sorry, that was an invalid player name.";	
  			return CricketManagerHelpers.getCricketTellSpeechletResponse(speechText);
        }
    	
    	
    	
    	
    	
    	
        //logic for getting from DynamoDB
    	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    	DynamoDBMapper mapper = new DynamoDBMapper(client);
    	getRuns=CricketManagerHelpers.getEntry(getPlayer,client);
    	speechText = getRuns+" Runs";
    	// Create the Simple card content.
			SimpleCard card = new SimpleCard();
			card.setTitle("Get runs");
			card.setContent(speechText);

 		// Create the plain text output.
 		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
 		speech.setText(speechText);

 		// Create reprompt
 		Reprompt reprompt = new Reprompt();
 		reprompt.setOutputSpeech(speech);
    	

     		return SpeechletResponse.newTellResponse(speech, card);
        }catch(NumberFormatException e){
     	   String speechText="Sorry, that was an invalid entry.";	
  			return CricketManagerHelpers.getCricketTellSpeechletResponse(speechText);
        }

        
    }
    
public SpeechletResponse getResetIntentResponse(Session session,Intent intent) {
        
    	String speechText = "The table is now clear";
    	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("CricketManagerTable");
        table.delete();
        try {
			table.waitForDelete();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

     // Create the Simple card content.
     		SimpleCard card = new SimpleCard();
     		card.setTitle("Reset");
     		card.setContent(speechText);
        
     		// Create the plain text output.
     		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
     		speech.setText(speechText);

     		// Create reprompt
     		Reprompt reprompt = new Reprompt();
     		reprompt.setOutputSpeech(speech);

     		return SpeechletResponse.newTellResponse(speech,card);

        
    }

public SpeechletResponse getHelpIntentResponse(Session session,Intent intent) {
    
	String speechText = COMPLETE_HELP;
	// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Help");
		card.setContent(speechText);
    	

 		// Create the plain text output.
 		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
 		speech.setText(speechText);

 		// Create reprompt
 		Reprompt reprompt = new Reprompt();
 		reprompt.setOutputSpeech(speech);

 		return SpeechletResponse.newAskResponse(speech, reprompt,card);

    
}

public SpeechletResponse exitIntentResponse(Session session,Intent intent) {
    
	String speechText = "Goodbye and have a nice day";
	// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("End");
		card.setContent(speechText);
    	

 		// Create the plain text output.
 		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
 		speech.setText(speechText);

 		// Create reprompt
 		Reprompt reprompt = new Reprompt();
 		reprompt.setOutputSpeech(speech);

 		return SpeechletResponse.newTellResponse(speech,card);

    
}
    
}
