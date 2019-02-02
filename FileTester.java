
import java.util.Date;

class FileTester {

    //static ZipOutputStream zout=null;

    public static  void main(String args[]) {

        if (args.length<3) {
            System.out.println("Неверно заданы параметры");
            System.out.println("Требуется: compress/decompress [имя исходного файла] [имя выходного файла]");
            return;
        }

        System.out.println("processors = "+ Runtime.getRuntime().availableProcessors());
        System.out.println("memory = "+ Runtime.getRuntime().maxMemory());


        MyThreadPool p1=null;

        if (args[0].equals("compress")) {
            p1 = new EncodeThreadPool(args[1],args[2], 10);
        }
        else if (args[0].equals("decompress")) {
            p1 = new DecodeThreadPool(args[1],args[2],10);
        }

        Date date = new Date();

        if (p1!=null) p1.createThreads();

        Date date2 = new Date();
        System.out.println((date2.getTime() - date.getTime())+" ms");
    }




}