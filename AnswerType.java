/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package questionanswering;

import java.util.*;
import java.io.*;
import com.cignoir.cabocha.*;
import com.cignoir.enums.PosDiv;
import com.cignoir.node.*;
import java.math.RoundingMode;
/**
 *
 * @author kosuke
 */
public class AnswerType {
    //おおまかに質問タイプを判定する
    public static final int ANSTYPE_FACT = 1;
    public static final int ANSTYPE_DEFINITION = 2;
    public static final int ANSTYPE_WHY = 3;
    public static final int ANSTYPE_HOW = 4;
    public static final int ANSTYPE_OTHERS = 5;
    
    //ファクト型ならば以下のいずれかを判定する
    public static final int ANSTYPE_TRIPLE_SUBJECT = 10;
    public static final int ANSTYPE_TRIPLE_PREDICATE = 11;
    public static final int ANSTYPE_TRIPLE_OBJECT = 12;
    
    
    public int DetectAnswerType(String question){
        
        return ANSTYPE_FACT;
    }
    
    public void ConvertRawFileToFeature(String inputFileName, String outputFileName, int classifier){
	    /*
	     * csvファイル(入力生データ,クラス名 の行の繰り返し)
	     * を素性形式に変換し、arffファイルとして出力
	     */
	 String line = "";
	    try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),"utf-8"));
            while((line = br.readLine()) != null){
	        ArrayList<Integer> output =  ExtractFeature(line.substring(0,line.indexOf(",")));
	        for(int i=0;i<output.size();i++){
			System.out.print(output.get(i) +",");
		}
		System.out.print(line.substring(line.indexOf(",")+1,line.length())+"\n");
	    }
            br.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
	}
	
    
    }
    
    
    public ArrayList<Integer> ExtractFeature(String question){
        ArrayList<String> tokens = new ArrayList<String>();
	
	String[] questionwords = {"いつ","どこ","誰","何","わけ","理由","原因","方法","定義","どっち","どれ","いくつ","なぜ","どうして","どう","どの","と","の","は","に","か","について","い","つ"};
	ArrayList<Integer> outputfeatures = new ArrayList<Integer>();
	for(int i=0;i<questionwords.length;i++){
		outputfeatures.add(0);
	}
        Cabocha cabocha = new Cabocha("C://Program Files/CaboCha/bin/cabocha.exe");
        try{
            Sentence stc = null;
            stc = cabocha.execute(question);
	    
	    for(int i=0;i<stc.getChunks().size();i++){
		for(int j=0;j<stc.getChunks().get(i).getTokens().size();j++){
		  tokens.add(stc.getChunks().get(i).getTokens().get(j).getSurface());  
		  //System.out.print(stc.getChunks().get(i).getTokens().get(j).getSurface()+",");
		}
		
	    }
        }catch (InterruptedException ex) {
	}catch (IOException e) {
		System.out.println("Error!");
        }
	//System.out.println("\n素性数："+outputfeatures.size());
	for(int i=0;i<tokens.size();i++){
	   for(int j=0;j<questionwords.length;j++){
	     if(questionwords[j].equals(tokens.get(i))){
                outputfeatures.set(j,1);
		break;
	     }
	     
	   }
	}
	
    
	return outputfeatures;
    }    
}
