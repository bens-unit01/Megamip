package  {
	/**

	 * @author user
	 */
	public class CustomButton extends BitmapButton {
		
		
		
		[Embed(source = "img/Btn_DPad_Forward.png")]
		public static var  ButtonForward : Class;
		
		[Embed(source = "img/Btn_DPad_Back.png")]
		public static var ButtonBackward : Class;
		
		[Embed(source = "img/Btn_DPad_Left.png")]
		public static var ButtonLeft : Class;
		
		[Embed(source = "img/Btn_DPad_Right.png")]
		public static var ButtonRight : Class;
		
		[Embed(source = "img/Background_DPad.png")]
		public static var DPadBackground : Class;

        [Embed(source = "img/Btn_Connect.png")]
		 public static var ButtonConnect : Class;
		 
		 [Embed(source = "img/Btn_Exit.png")]
		 public static var ButtonExit : Class;
    
       public  function CustomButton(ImageData:Class){
		
		   super(ImageData);
		}
		
			
		
	}
}
