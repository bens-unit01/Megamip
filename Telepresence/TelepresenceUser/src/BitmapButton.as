package  {
	/**
	 * @author user
	 * 
	 */
	 
	    import flash.display.Bitmap;
		import flash.display.BitmapData;
		import flash.display.Sprite;
		import flash.events.MouseEvent;
	public class BitmapButton extends Sprite {
		
		


	private var bitmapData:BitmapData;
    private const  THRESHOLD:Number = 0;
		
		public function BitmapButton(ImageData:Class) 
		{
		    var image:Bitmap = new ImageData();
		    addChild(image);
		         
		    bitmapData = image.bitmapData;
		     
		  //  addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
		  //  addEventListener(MouseEvent.CLICK, onClick);
		}
		
		
	}
}
