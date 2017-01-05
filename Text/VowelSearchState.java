import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Set;

public class VowelSearchState implements SearchState{
	
	private static final String ADD_VOWELS = "add vowels";
	final String sentence;
	final String newSentence;
	final String remainingSentence;
	final String parent;
	final int counter;
	final double totalCost;
	
	private VowelSearchState(String sentence, String newSentence, String remainingSentence, String parent, int counter, double totalCost){
		this.sentence = sentence;
		this.newSentence = newSentence;
		this.remainingSentence = remainingSentence;
		this.parent = parent;
		this.counter = counter;
		this.totalCost = totalCost;
	}
	
	public static class Builder extends SearchState.Builder{
		public SearchState makeInitialState(String sentence) throws IllegalArgumentException{
			try{
				String temp = sentence;
				int wordct = (temp.length() - temp.replace(" ","").length()) + 1;
				System.out.println("There are " + wordct + " words we need to add vowels to.");
				return new VowelSearchState(sentence, "", sentence, LangUtil.SENTENCE_BEGIN, wordct, 0.0);
			} catch (IllegalFormatException ex){
				throw new IllegalArgumentException("Need sentence for initial state, got " + sentence);
			}
		}
	}
	
	public String getBestFit(String remaining, String parent){
		String novowels = "", result = "";
		if(counter!=1){
		novowels = remaining.substring(0, remaining.indexOf(" "));
		}else{
			novowels = remaining;
		}
		//System.out.println("The no vowel word we are looking up is: " + novowels +".");
		Set<String> candidates = ExpansionDictionary.getInstance().lookup(novowels);
		double ugscore = 0.0, bgscore = 0.0, total = 0.0, top = 0.0;
		for(String s: candidates){
			/*
			 * Get the ugscore, bgscore, and total for candidate string s
			 */
			//System.out.println("==================================");
			//System.out.println("The string being evaluated is: " + s);
			ugscore = UnigramModel.getInstance().cost(s);
			//System.out.println("The ug score is: " + ugscore);
			bgscore = SmoothedBigramModel.getInstance().cost(parent, s);
			//System.out.println("The bg score is: " + bgscore);
			if(s.length() <= 6){
				total = (ugscore + bgscore) * (1.0/(double)s.length());
			}else{
				total = (ugscore + bgscore);
			}
			//System.out.println("The total score is: " + total + "\n==================================");
			/*
			 * If we have a total value less than the old top val, we have a new top val
			 * note top refers to it being top choice, not the highest score
			 */
			if(total!=0.0){
				if(top == 0.0){
					top = total;
					result = s;
				}else if(total < top){
					top = total;
					result = s;
				}
			}
		}
		//System.out.println("The new word with vowels is: " + result + " with a cost of: " + top);
		return result;
	}
	
	@Override
	public int hashCode(){
		return (int)totalCost ^ sentence.length() ^ newSentence.length() ^ remainingSentence.length() ^ parent.length();
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(other == this) return true;
		if(!(other instanceof VowelSearchState)) return false;
		VowelSearchState otherState = (VowelSearchState) other;
		return (otherState.totalCost == this.totalCost && sentence.equals(otherState.sentence) && newSentence.equals(otherState.newSentence) && remainingSentence.equals(otherState.remainingSentence) && parent.equals(otherState.parent));
	}
	
	@Override
	public boolean isGoal() {
		if(counter == 0){
			System.out.println("The sentence with vowels is: " + newSentence);
			System.out.println("The total cost was: " + totalCost);
			return true;
		}else
			return false;
	}

	@Override
	public Collection<String> getApplicableActions() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add(ADD_VOWELS);
		return actions;
	}

	@Override
	public double getActionCost(String action) {
		if(action.equals(ADD_VOWELS)){
			String w = getBestFit(remainingSentence, parent);
			return SmoothedBigramModel.getInstance().cost(parent, w);
		}else
		return 0;
	}

	@Override
	public SearchState applyAction(String action) {
		if(action.equals(ADD_VOWELS)){
			String w = getBestFit(remainingSentence, parent);
			double cost = SmoothedBigramModel.getInstance().cost(parent, w);
			String temp = remainingSentence.substring(remainingSentence.indexOf(" ")+1, remainingSentence.length());
			//System.out.println("xxxxxxxxxxxxxxxxxx\n" + remainingSentence + "\nNext:"+ temp  +"\nxxxxxxxxxxxxxxxxxx");
			return new VowelSearchState(sentence, newSentence.concat(w + " "), temp, w, counter - 1, totalCost+cost);
		}else
		return this;
	}

}
