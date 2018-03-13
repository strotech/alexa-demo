package com.amazonaws.dem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class CricketManagerHelpers {
	
	public static void createCricketManagerTable(String tableName,AmazonDynamoDB client) throws InterruptedException{
		List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("DataPlayer").withAttributeType("S"));

		List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("DataPlayer").withKeyType(KeyType.HASH));

		        
		CreateTableRequest request = new CreateTableRequest()
		        .withTableName(tableName)
		        .withKeySchema(keySchema)
		        .withAttributeDefinitions(attributeDefinitions)
		        .withProvisionedThroughput(new ProvisionedThroughput()
		            .withReadCapacityUnits(5L)
		            .withWriteCapacityUnits(6L));

		TableUtils.createTableIfNotExists(client,request);

		TableUtils.waitUntilActive(client,tableName);
	}

	public static int getRuns(String player,AmazonDynamoDB client){
		
		
		
				
        Map<String, AttributeValue> zse = new HashMap<String, AttributeValue>();
        zse.put(":val1", new AttributeValue().withS(player));
   

        
        ScanRequest scanRequest = new ScanRequest()
        		.withTableName("CricketManagerTable")
        		.withProjectionExpression("DataRuns")
        		.withExpressionAttributeValues(zse)
        		.withFilterExpression("DataPlayer = :val1");
        
        ScanResult scanResult = client.scan(scanRequest);        
        List<Map<String,AttributeValue>> results = scanResult.getItems();
        
        double sum=0;
        
        for(Map<String,AttributeValue> map:results){
        	try{
        		AttributeValue rnValue=map.get("DataRuns");
        		String rnString=amtValue.getS();
        		runs=Integer.parseInt(rnString);
        	
        	}catch(Exception e){
        		runs=0;
        	}
        	
        }
       
        return runs;
	
	}
	
	public static SpeechletResponse getCricketTellSpeechletResponse(String speechText){
				// Create the Simple card content.
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
		    	
	}
	
}
