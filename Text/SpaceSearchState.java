import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatException;

public class SpaceSearchState implements SearchState {
	
	private static final String ADD_SPACE = "add space";
	final String sentence;
	final String newSentence;
	final String remainingSentence;
	final String parent;
	final double totalCost;


private SpaceSearchState(String sentence, String newSentence, String remainingSentence, String parent, double totalCost){
	this.sentence = sentence;
	this.newSentence = newSentence;
	this.remainingSentence = remainingSentence;
	this.parent = parent;
	this.totalCost = totalCost;
	}

public static class Builder extends SearchState.Builder{
	public SearchState makeInitialState(String sentence) throws IllegalArgumentException{
		try{
			return new SpaceSearchState(sentence, "", sentence, LangUtil.SENTENCE_BEGIN, 0.0);
		}catch (IllegalFormatException ex){
			throw new IllegalArgumentException("Need sentence for initial state, got " + sentence);
		}
	}
}

public String getNextWord(String remaining, String parent){
	 String topcandidate = "", temp = "";
	 double ugscore = 0.0, bgscore = 0.0, total= 0.0, top = 0.0;
	 int length = remaining.length(), bound = 0;
	 if(length < 10){
		 bound = length;
	 }else{
		 bound = 10;
	 }
	 //get words less than the bound. Words > 10 are usually junk as avg word length
	 //in english is approx 5 chars
	 for(int i = 1; i<=bound; i++){
		 //System.out.println("================================================");
		 //get the temp string by taking substring of remaining sentence: this works
		 temp = remaining.substring(0, i);
		 //System.out.println("the string being evaluated is: " + temp);
		 //get the unigram score for temp
		 ugscore = UnigramModel.getInstance().cost(temp);
		 //System.out.println("The ug score is: " + ugscore);
		 //get the bigram score for temp in reference to it's parent
		 bgscore = SmoothedBigramModel.getInstance().cost(parent, temp);
		 //System.out.println("The bg score is: " + bgscore);
		 //get the total score, a weighted avg of the sum of the ug score and bg score divided by length of the string
		 //only weigh if the string length is 5 or less. 
		 if(temp.length()<=6){
		 total = (ugscore + bgscore) * (1.0/(double)temp.length());
		 }else{
			 total = (ugscore + bgscore);
		 }
		 //System.out.println("The cost associated with it is: " + total + "\n===============================================");
		 /*
		  * If there's no top, we're at the start of the loop. Initialize top to be the first
		  * total sum of the weighted score and candidate to be the associated word
		  */
		 if(total!=0.0){
			 if(top == 0.0){
				 top = total;
				 topcandidate = temp;
			 }else if(top > total){
			 /*
			  * The top value is more than the total for the new candidate, so we have new top
			  */
				 top = total;
				 topcandidate = temp;
			 }
		 }
		
	 }
	 //System.out.println("Top candidate is: " + topcandidate + " with a cost of: " + top);
	 return topcandidate;
}

@Override
public int hashCode(){
	return (int)totalCost ^ sentence.length() ^ newSentence.length() ^ remainingSentence.length() ^ parent.length();
}

@Override
public boolean equals(Object other){
	if(other == null) return false;
	if(other == this) return true;
	if(!(other instanceof SpaceSearchState)) return false;
	SpaceSearchState otherState = (SpaceSearchState) other;
	return(otherState.totalCost == this.totalCost && sentence.equals(otherState.sentence) && newSentence.equals(otherState.newSentence) && remainingSentence.equals(otherState.remainingSentence) && parent.equals(otherState.parent));
}

@Override
public boolean isGoal() {
	if(remainingSentence.isEmpty()){
		System.out.println("The separated sentence is: " + newSentence);
		System.out.println("The total cost was: " + totalCost);
		return true;
	}else
		return false;
}


@Override
public Collection<String> getApplicableActions() {
	ArrayList<String> actions = new ArrayList<String>();
	actions.add(ADD_SPACE);
	return actions;
}


@Override
public double getActionCost(String action) {
	if(action.equals(ADD_SPACE)){
		String w = getNextWord(remainingSentence, parent);
		double j = UnigramModel.getInstance().cost(w);
		return j;
	}else
		return 0;
}


@Override
public SearchState applyAction(String action) {
	if(action.equals(ADD_SPACE)){
		String w = getNextWord(remainingSentence, parent);
		double j = UnigramModel.getInstance().cost(w);
		int i = w.length();
		return new SpaceSearchState(sentence, newSentence.concat(w + " "), remainingSentence.substring(i), w, totalCost + j);
	}else
		return this;
}


}