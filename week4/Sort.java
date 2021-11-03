import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Sort {
    public static class FrequencySortMap extends Mapper<Object, Text, IntWritable, Text>
  {
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException
    {
      String line = value.toString();
      StringTokenizer stringTokenizer = new StringTokenizer(line);
      {
        while (stringTokenizer.hasMoreTokens()) {

            int frequency = 0;
            String word = "";

            word = stringTokenizer.nextToken();
            if (stringTokenizer.hasMoreTokens()) {
                frequency = Integer.parseInt(stringTokenizer.nextToken());
                context.write(new IntWritable(frequency), new Text(word));
            }
        }
      }
    }
  }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "sort");
        job.setJarByClass(Sort.class);
        job.setMapperClass(FrequencySortMap.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}