# Shape AI
An AI that can recognise shape from drawing using perceptron algorithm.
It have a 20x20 pixel drawing interface. Each pixel is a input for the AI.

## Version
* V1
  This version only have one neurone to generate the output.
* V2
  This version have not only one output neurone, it have 4 more hidden neurone.

## How it work
As the drawing box is 20x20, the box will store as a input by 1x400 matrix.
And every input(pixel) is link to the final neurone with a weight and store as a 400x1 matrix.
The input matrix (1x400) will multiply with the weight matrix(400x1) and if the number is greater than the neurone bias, neurone will fire, else not.
First, user draw the shape on the drawing box app.
Then, user can train the AI by clicking Train in **Action > Train** on the menu bar.
After the button is click, the AI will determine the shape of the drawing. Then a pronmt window will pop up and ask for the correct shape.
* If the neurone doesn't fire when it should (circle drawn but AI detected square), the input matrix will add up to the weight matrix.
* If the newrone fire when it shouldn't (square drawn but AI detected circle), the input matrix will subtract from the weight matrix.
