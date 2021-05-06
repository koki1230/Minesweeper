import java.util.Scanner;
import java.util.Random;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Minesweeper {

  static int num_mine = 0;  //爆弾の数
  static int width = 0;     //マスの幅
  static boolean[][] open = null;    //マスが開いているか
  static boolean[][] mine = null;   // 爆弾であるか
  static int[][] aroud_mine = null;     //周りの爆弾の数

  public static void main(String[] args) {
    set_difficulty();  //難易度設定
    init_board();  //盤面初期化と爆弾設置
    LocalTime time1 = LocalTime.now(); //開始時間取得
    while (clear_cheak()){  //クリア条件達成でループ終了
      print_board();  //盤面表示
      open_squares();  //マス開け処理
      safety_open();  //0の周りを開ける処理
    }
    LocalTime time2 = LocalTime.now();  //終了時間取得
    System.out.println("ゲームクリア！！おめでとう");
    System.out.println("クリアタイム："+ChronoUnit.SECONDS.between(time1,time2)+"秒");  //開始時間と終了時間の差を取り表示
  }

  public static void set_difficulty(){  //難易度設定
    Scanner scanner = new Scanner(System.in);
    while (num_mine == 0) {
      System.out.println("難易度を選択してださい 1<2<3");
      String diff = scanner.next();
      switch(diff){
        case "1":
          width = 6;
          num_mine = 4;
          System.out.println("難易度1（"+width+"*"+width+" 地雷："+num_mine+"）になりました");
          break;
        case "2":
          width = 9;
          num_mine = 10;
          System.out.println("難易度2（"+width+"*"+width+" 地雷："+num_mine+"）になりました");
          break;
        case "3":
          width = 16;
          num_mine = 40;
          System.out.println("難易度3（"+width+"*"+width+" 地雷："+num_mine+"）になりました");
          break;
        default:
          System.out.println("1,2,3を入力してくだい");
          break;
      }
    open = new boolean[width+2][width+2];
    mine = new boolean[width+2][width+2];
    aroud_mine = new int[width+2][width+2];
    }
  }

  public static void init_board() {  //盤面初期化と爆弾設置
    Random rand = new Random();
    for (int x = 0; x < width+2 ; x++){
      for (int y = 0; y < width+2 ; y++){
        open[x][y] = false;
        mine[x][y] = false;
        aroud_mine[x][y] = 0;
      }
    }
    for (int i = 0;i < num_mine;i++ ){  //爆弾の設置
      int x,y;
      do {
        x = rand.nextInt(width)  + 1; // 1~widthの乱数
        y = rand.nextInt(width)  + 1;
      }while(mine[x][y]);
      mine[x][y] = true;
      aroud_mine[x+1][y+1] += 1;  //爆弾の周りのマスのカウント1増加
      aroud_mine[x][y+1] += 1;
      aroud_mine[x-1][y+1] += 1;
      aroud_mine[x+1][y] += 1;
      aroud_mine[x-1][y] += 1;
      aroud_mine[x+1][y-1] += 1;
      aroud_mine[x][y-1] += 1;
      aroud_mine[x-1][y-1] += 1;
    }
  }

  public static void print_board(){  //盤面表示
    System.out.print("    |");
    for (int i = 1;i < width + 1 ;i++){
      System.out.printf(" %2d |",i);
    }
    System.out.print("\n");
    for (int i = 1;i < width + 1 ;i++){
      System.out.printf(" %2d |",i);
      for (int j = 1;j < width + 1 ;j++){
        System.out.printf(" %2s |",check_squares(i , j));
      }
      System.out.print("\n");
    }
  }

  public static String check_squares(int x,int y){  //マス表示
    if (open[x][y]){   //マスの状態確認
      if (mine[x][y]){ //ゲームオーバー後の爆弾表示
        return "*";
      }else {
        return Integer.valueOf(aroud_mine[x][y]).toString();
      }
    }else {
      return "･";
    }

  }

  public static void open_squares(){  //マス開け処理
    Scanner scanner = new Scanner(System.in);
    while(true){
      System.out.println("座標を入力してください ex:2,3");
      String coordinate  = scanner.next();  //入力された座標を変換する
      String[] position = coordinate.split(",");
      try{
        int x = Integer.parseInt(position[0]);
        int y = Integer.parseInt(position[1]);
        if (x > 0 && x < width + 1 && y > 0 && y < width + 1){
          if (mine[x][y]){  //爆弾であるか？
            explosion();
          } else{
            open[x][y] = true;  //マスの状態変更
            break;
          }
        } else{
          System.out.println("1から"+ width + "までの座標を入力してください");
        }
      }
      catch(NumberFormatException e){  //数値以外を入力した場合
        System.out.println("数値で座標を入力してください");
      }
      catch(ArrayIndexOutOfBoundsException e){  //カンマなどがない場合
        System.out.println("例のように座標を入力してください");
      }
    }
  }

  public static void explosion(){ //地雷を選びゲームオーバーの時
    System.out.println("ドカーーん！！地雷が爆発しました");
    for (int x = 1; x < width+1 ; x++){
      for (int y = 1; y < width+1 ; y++){
        open[x][y] = true;
      }
    }
    print_board();
    System.exit(0);
  }

  public static void safety_open(){  //0の周りを開ける処理
    for (int i = 0;i < width - 1;i++){  //0から0がでてきたときのため
      for (int x = 1; x < width + 1; x++){
        for (int y = 1; y < width + 1; y++){
          if(open[x][y] && aroud_mine[x][y] == 0){ //0かつ開いているマス
            open[x+1][y+1] = true;
            open[x+1][y] = true;
            open[x+1][y-1] = true;
            open[x][y+1] = true;
            open[x][y-1] = true;
            open[x-1][y+1] = true;
            open[x-1][y] = true;
            open[x-1][y-1] = true;
          }
        }
      }
    }
  }

  public static boolean clear_cheak(){ //クリア判定
    for (int x = 1; x < width+1 ; x++){
      for (int y = 1; y < width+1 ; y++){
        if(!mine[x][y]){  //爆弾ではないマスがすべて開いていたらクリア
          if(!open[x][y])  //開いていないマスがある
            return true;  //ループが続く
        }
      }
    }
    for (int x = 1; x < width+1 ; x++){
      for (int y = 1; y < width+1 ; y++){
        open[x][y] = true;  //全てのマスをオープンし表示
      }
    }
    print_board();
    return false;   //ループ終了ゲームクリア
  }

}
