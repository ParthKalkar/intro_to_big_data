
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Random;
import java.util.Scanner;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

  public static class TokenizerMapper
       extends Mapper<Object, Text, IntWritable, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
                    
                    
      String line = value.toString();
      StringTokenizer stringTokenizer = new StringTokenizer(line);
      {
        int class_id = 0; 
        double x = 0;
        double y = 0;
        	
        String[] l = line.split(",");
        x = Double.parseDouble(l[0]);
        y = Double.parseDouble(l[1]);
        if (x*x+y*y<1){
        	class_id = 1;
        }
        context.write(new IntWritable(class_id), one);
      }
    }
  }

  public static class IntSumReducer
       extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(IntWritable key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  static void point_gen(String path,int n){
  	final Random random = new Random();
  	
  	try {
  	FileWriter myWriter = new FileWriter(path);
  	for (int i=0;i<n;i=i+1){
  		 myWriter.write(random.nextDouble()+","+random.nextDouble()+"\n");
  	
  	}
  	myWriter.close();
  	} catch (IOException e) {System.out.println("An error occurred.");e.printStackTrace();}
  };
  
  static void pi_eval(String path){
  
  	try {
    	File obj = new File(path);
  	Scanner myReader = new Scanner(obj);
  	double in_circle = Double.parseDouble(myReader.nextLine().split("\t")[1]);
  	double out_circle = Double.parseDouble(myReader.nextLine().split("\t")[1]);
  	System.out.println("pi = " + Double.toString(in_circle/out_circle));
  	} catch (FileNotFoundException e) {
      System.out.println("An error occurred.");e.printStackTrace();}
  };

  public static void main(String[] args) throws Exception {
    
  //  point_gen(args[0],Integer.parseInt(args[2]));
  //  System.out.println("copy");


    
    /*Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);*/
    pi_eval(args[1]);
  }
}
