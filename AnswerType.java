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
    
    //postListは名義変数
    //public static String[] posList = {"名詞", "助詞", "動詞", "連体詞", "接頭詞", "副詞", "感動詞", "記号", "形容詞", "助動詞"};  //一番シンプルな分類
    public static String[] posList = {"名詞-一般", "名詞-固有名詞", "名詞-代名詞", "名詞-非自立", "名詞-サ変接続", "名詞-形容動詞語幹","名詞-ナイ形容詞語幹", "名詞-副詞可能", "名詞-接尾", "名詞-数",
		 "助詞-係助詞", "助詞-格助詞", "助詞-副助詞", "助詞-連体化","助詞-副詞化", "助詞-並立助詞", "助詞-終助詞", "助詞-接続助詞", "助詞-副助詞／並立助詞／終助詞",
		 "動詞-自立", "動詞-非自立", "動詞-接尾",
		 "連体詞-*",  //これ以上細分化できない
		 "接頭詞-名詞接続","接頭詞-数接続",
		 "副詞-一般","副詞-助詞類接続",
		 "感動詞-*",  //これ以上細分化できない
		 "記号-一般","記号-括弧開","記号-括弧閉","記号-読点",
		 "形容詞-自立","形容詞-非自立",
		 "助動詞-*"};  //これ以上細分化できない
   
    
    
    //ansTypeは名義変数
    public static String[] ansType = {"FACT/NAME", "FACT/SIZE", "FACT/LOC", "FACT/DATE", "FACT/PER", "FACT/ORG"};//, "WHY", "HOW", "DEFINITION"};
    
    //questionWordsはInteger型
    public static String[] questionWords = {"いつ","どこ","誰","何","わけ","理由","原因","方法","定義","数","名前","中","どっち","どれ","いくつ","一番","いちばん","なぜ","どうして","どう","どの","で","と","の","は","に","か","について","い","つ","が","方","名","物","もの","者","人","約","呼ぶ","作者","名称","県","国","都市","著者","最","くらい","さ","れ","る","し","た","およそ"};
    
    public int DetectAnswerType(String question){
        
        return ANSTYPE_FACT;
    }
    
    public void ConvertRawFileToFeatureFile(String datasetname, String inputFileName, String outputFileName, int classifier){
	    /* 素性の設計
	     * 入力csvファイル
	     * (入力生データ,クラス名) の行の繰り返し
	     * を素性形式に変換し、arffファイルとして出力
	     */
	/*-------------------------------素性の構築-------------------------------------------------*/
	 /* データセット名 */
	 String arfffile_template = "@relation " + datasetname + "\r\n";
	
	 /*  questionWords -Integer型, 後ろから数えて現れた形態素レベルでの位置- */
	 for(int i=0;i<questionWords.length;i++){
            arfffile_template = arfffile_template + "@attribute 「" + questionWords[i]  +"」の現れた位置 " + " integer" + "\r\n";
	 }
	 
	 /*  postype_1 -名義変数, 末尾の形態素の品詞- */
	 arfffile_template = arfffile_template + "@attribute postype_1{";  //開始
		 for(int i=0;i<posList.length-1;i++){
			 arfffile_template = arfffile_template + posList[i] + ",";
		 }
                 arfffile_template = arfffile_template + posList[posList.length-1] + "}\r\n";  //終了
		 
         /*  postype_2 -名義変数, 後ろから2番目の形態素の品詞- */
         arfffile_template = arfffile_template + "@attribute postype_2{";  //開始
		 for(int i=0;i<posList.length-1;i++){
			 arfffile_template = arfffile_template + posList[i] + ",";
		 }
                 arfffile_template = arfffile_template + posList[posList.length-1] + "}\r\n";  //終了
        
         /*  morph_len -Integer型, 形態素の長さ- */
         arfffile_template = arfffile_template + "@attribute morph_len integer\r\n";  //開始
	 
	 /*  string_len -Integer型, 質問文の文字数- */
         arfffile_template = arfffile_template + "@attribute string_len integer\r\n";  //開始
	 
	 /*  anstype -名義変数, 回答タイプ- */
	  arfffile_template = arfffile_template + "@attribute anstype{";  //開始
		 for(int i=0;i<ansType.length-1;i++){
			 arfffile_template = arfffile_template + ansType[i] + ",";
		 }
                 arfffile_template = arfffile_template + ansType[ansType.length-1] + "}\r\n@data\r\n";  //終了
	
	/*------------------------------素性構築/おわり-------------------------------------------------------------------------------*/	 
		 
	 
	    try {
	    String line = "";
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),"utf-8"));
	    File file = new File(outputFileName);
            FileWriter fw = new FileWriter(file, true); 
	    fw.write(arfffile_template);            //@dataより手前を書き出す
	    //素性変換後のデータをarffファイルへ出力
            while((line = br.readLine()) != null){
		String inputstr = line.substring(0,line.indexOf(","));
	        ArrayList<String> output =  ExtractFeature(inputstr);
		//System.out.print(inputstr+",");
	        for(int i=0;i<output.size();i++){
			//System.out.print(output.get(i) +",");
			fw.write(output.get(i) +",");
		}
		//System.out.print(line.substring(line.indexOf(",")+1,line.length())+"\n");
		fw.write(line.substring(line.indexOf(",")+1,line.length())+"\r\n");  //回答タイプを追加
		
	    }
            br.close();
	    fw.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
	}
	
    
    }
    
    
    public ArrayList<String> ExtractFeature(String question){
	
	/*-------------------------出力する素性ベクトルの初期化------------------------------------*/
	ArrayList<String> outputfeatures = new ArrayList<String>();
	for(int i=0;i<questionWords.length;i++){	//キーワード以降はaddしていく
		outputfeatures.add("0");
	}	    
	    
        ArrayList<String> tokens_surface = new ArrayList<String>();    //入力文（形態素解析後）の各々の表層文字列を格納
	ArrayList<String> tokens_pos = new ArrayList<String>();       //入力文（形態素解析後）の各々の形態素品詞列を格納

	
	
	
        Cabocha cabocha = new Cabocha("C://Program Files/CaboCha/bin/cabocha.exe");
        try{
            Sentence stc = null;
            stc = cabocha.execute(question.replaceAll(" ", "").replaceAll("　", ""));
	    int chunksize = stc.getChunks().size();
	    for(int i=0;i<chunksize;i++){
		for(int j=0;j<stc.getChunks().get(i).getTokens().size();j++){
		  tokens_surface.add(stc.getChunks().get(i).getTokens().get(j).getSurface());     //前から順番に形態素の表層文字列を格納
		  if(!stc.getChunks().get(i).getTokens().get(j).getPos().contains("?")){
			String pos = stc.getChunks().get(i).getTokens().get(j).getPos();
			String pos_second = pos.substring(0,pos.lastIndexOf("-"));
			tokens_pos.add(pos_second);    //前から順番に形態素の品詞を格納
			  
		  }
		  //System.out.print(stc.getChunks().get(i).getTokens().get(j).getSurface()+",");
		}
	    }
	    
	    //System.out.println(pos.get(pos.size()-2)+"\n"+pos.get(pos.size()-1));
	    
        }catch (InterruptedException ex) {
	}catch (IOException e) {
		System.out.println("Error!");
        }
	//System.out.println("\n素性数："+outputfeatures.size());
	
	/*-------------------------素性値を代入--------------------------------------------*/
	
	
	//questionWordsの形態素位置(後ろから数えて)
	for(int i=0;i<tokens_surface.size();i++){
	   for(int j=0;j<questionWords.length;j++){
	     if(questionWords[j].equals(tokens_surface.get(i))){
                outputfeatures.set(j,Integer.toString(tokens_surface.size()-i));  //位置を記録 末尾を1として数える
		break;
	     }
	   }
	}
	
	/* 末尾の形態素の品詞 */
	outputfeatures.add(tokens_pos.get(tokens_pos.size()-1));
	
	/* 後ろから2番目の形態素の品詞 */
	outputfeatures.add(tokens_pos.get(tokens_pos.size()-2));
	
	/* 質問文の形態素の長さ */
	outputfeatures.add(Integer.toString(tokens_pos.size()));
	
	/* 質問文の文字列の長さ */
	outputfeatures.add(Integer.toString(question.length()));
	
	return outputfeatures;
    }    
}