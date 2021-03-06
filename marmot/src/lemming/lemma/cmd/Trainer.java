// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import lemming.lemma.LemmaInstance;
import lemming.lemma.LemmaOptions;
import lemming.lemma.LemmaResult;
import lemming.lemma.Lemmatizer;
import lemming.lemma.LemmatizerTrainer;
import marmot.core.Sequence;
import marmot.core.Token;
import marmot.morph.Word;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;

public class Trainer {
	
	public static void main(String[] args) {
		
		String model_type = args[0];
		String options_string = args[1];
		String output_file = args[2];
		String train_file = args[3];
		
		Lemmatizer lemmatizer = train(model_type, options_string, train_file);
		
		for (int i=4; i < args.length; i += 2) {
			String test_file = args[i];
			String pred_file = args[i + 1];
			
			test(lemmatizer, test_file);
			annotate(lemmatizer, test_file, pred_file);	
		}
		
		FileUtils.saveToFile(lemmatizer, output_file);
	}
	
	static void annotate(Lemmatizer lemmatizer, String test_file, String pred_file) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pred_file));
			for (Sequence sequence : new SentenceReader(test_file)) {
				for (Token token : sequence) {
					LemmaInstance instance = LemmaInstance.getInstance((Word) token);
					String plemma = lemmatizer.lemmatize(instance);
					
					writer.write(String.format("%s\t%s\n", instance, plemma));
				}
				writer.write('\n');				
			}
			writer.close();			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	public static Lemmatizer train(String model_type, String options_string,
			String train_file) {
		
		LemmatizerTrainer trainer;
		try {
			Class<?> trainer_class = Class.forName(model_type);
			trainer = (LemmatizerTrainer) trainer_class.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
		
		LemmaOptions options = trainer.getOptions();	
		options.readArguments(options_string);
		
		Logger logger = Logger.getLogger(Trainer.class.getName());
		logger.info(options.report());

		List<LemmaInstance> training_instances = LemmaInstance.getInstances(new SentenceReader(train_file), options.getLimit());
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		return lemmatizer;
	}

	public static void test(Lemmatizer lemmatizer, String test_file) {
		LemmaResult.logTest(lemmatizer, test_file, 50);
	}

}
