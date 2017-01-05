Mtthw Rsch (Matthew Resch) mar457 - Text Reconstruction

Notes:

-All prints except for the isGoal() prints were used for debugging purposes (namely to check that I've gotten the correct values in the next state)
-I couldn't figure out if the Node was supposed to print the result, so I added a field to the state in order to maintain a string of the "new" string which is printed at the end
-I use a weighted avg of ug and bg values in order to find the best possible match for a word as well as seperator, but count the "cost" only of ug or bg in order to get the 
minimal ug/bg value
	-> using a weighted avg of both allows for more accurate choices when it comes to deciding where to put a space or choosing a new word
-Check comments of code for anything I forgot to outline here
