import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import mopaqlib.Encryption;
import mopaqlib.MoPaQ;




public class TestMPQ {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		byte[] header=new byte[2048];
		byte[] newdata;
		System.out.println("Starting test");

		//RandomAccessFile source=new RandomAccessFile("fpstest.SC2Map","r" );
		//source.read(header);
		MoPaQ test=new MoPaQ(new File("OptimzeSmallerFiles.sc2map"));
		//source.close();
		
		System.out.println("Unidentified files: " + test.unidentifiedFiles);
		System.out.println();
		test.printDebugInfo();
		System.out.println();
		System.out.println();
		
		test.extractAll("extracted");
		
		System.out.println("Saving map as newtestmap_out.SC2MAP");
		RandomAccessFile newdatas=new RandomAccessFile("deDE.SC2Data_LocalizedData_GameStrings_Neu.txt", "r");
		newdata=new byte[(int) newdatas.length()];
		newdatas.read(newdata);
		newdatas.close();

		test.writeFile("deDE.SC2Data\\LocalizedData\\GameStrings.txt", newdata);
		test.writeFile("mynewfile", newdata);
		
		//RandomAccessFile destination=new RandomAccessFile("fpstestsmall.SC2Map","rw" );
		//destination.write(header);

		//test.save(new File("fpstestsmall.SC2Map"));
		
//		destination.close();
		
		System.out.println();
		test.printDebugInfo();
		System.out.println();
		System.out.println();
		
		System.out.println("Test ended");
		//System.in.read();
		/*byte[] testdata=new byte[16];
		for(int i=0;i<testdata.length;i++)
		{
			testdata[i]=(byte)i;
		}
		
		
		Encryption.encrypt(testdata, 1234);*/
	}

}
