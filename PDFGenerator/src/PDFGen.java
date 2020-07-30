import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.amihaiemil.eoyaml.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.cli.*;

public class PDFGen {

	public static void main(String[] args) throws DocumentException, IOException {
		Options options = new Options();

		Option input = new Option("i", "input", true, "data file name (.yml format file)");
		input.setRequired(true);
		options.addOption(input);

		Option output_file = new Option("o", "output", true, "output file name (.pdf format file)");
		output_file.setRequired(true);
		options.addOption(output_file);

		Option template_file = new Option("t", "template", true, "template file name (.pdf format file)");
		template_file.setRequired(true);
		options.addOption(template_file);

		Option ex_font = new Option("f", "font", true, "font file name (.ttf format file)");
		ex_font.setRequired(true);
		options.addOption(ex_font);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("my-program", options);
			System.exit(1);
			return;
		}

		String output = cmd.getOptionValue("output");
		String template = cmd.getOptionValue("template");
		String font = cmd.getOptionValue("font");
		String data = cmd.getOptionValue("input");

		System.out.println("Output file: " + output);
		System.out.println("Template file: " + template);
		System.out.println("Font file: " + font);
		System.out.println("Input file: " + data);

		PDFGen obj = new PDFGen();
		obj.genPDF(template, output, font, data);
		System.out.println("\nPDF file generated.");

	}

	public void genPDF(String template, String output, String font, String data) throws DocumentException, IOException {
		PdfReader reader = new PdfReader(template);
		OutputStream os = new FileOutputStream(output);
		PdfStamper stamper = new PdfStamper(reader, os);
		BaseFont input_font = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		AcroFields form = stamper.getAcroFields();
		AcroFields.Item item;

		// Set font
		form.addSubstitutionFont(input_font);

		// Center every field item
		item = form.getFieldItem("name");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("id");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("company");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("department");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("name_2");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("id_2");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("company_2");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("department_2");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("total_work");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("total_leave");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("total_late");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("total_absent");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		item = form.getFieldItem("total_hours");
		item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
		for (int i = 1; i <= 50; i++) {
			item = form.getFieldItem("date" + "_" + i);
			item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
			item = form.getFieldItem("in" + "_" + i);
			item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
			item = form.getFieldItem("out" + "_" + i);
			item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
			item = form.getFieldItem("job" + "_" + i);
			item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));
			item = form.getFieldItem("note" + "_" + i);
			item.getMerged(0).put(PdfName.Q, new PdfNumber(PdfFormField.Q_CENTER));

		}

		// Create YamlMapping and YamlSequence with .yml data file
		YamlMapping data_file = Yaml.createYamlInput(new File(data)).readYamlMapping();
		YamlSequence row = data_file.yamlSequence("row");

		int i = 1;
		long wtime_count = 0;
		long work_time = 0;
		int break_time = 0;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date work_in = null;
		Date work_out = null;

		// Merge with data file and set font size
		form.setField("name", data_file.string("name"));
		form.setField("id", data_file.string("id"));
		form.setField("company", data_file.string("company"));
		form.setField("department", data_file.string("department"));
		form.setField("name_2", data_file.string("name"));
		form.setField("id_2", data_file.string("id"));
		form.setField("company_2", data_file.string("company"));
		form.setField("department_2", data_file.string("department"));
		form.setField("total_work", data_file.string("total_work"));
		form.setField("total_leave", data_file.string("total_leave"));
		form.setField("total_late", data_file.string("total_late"));
		form.setField("total_absent", data_file.string("total_absent"));

		for (YamlNode data_row : row) {
			YamlMapping str = data_row.asMapping();
			form.setField("date" + "_" + i, str.string("date"));
			form.setField("in" + "_" + i, str.string("in"));
			form.setField("out" + "_" + i, str.string("out"));
			form.setField("job" + "_" + i, str.string("job"));
			form.setField("note" + "_" + i, str.string("note"));
			try {
				work_in = format.parse(str.string("in"));
				work_out = format.parse(str.string("out"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long time_tmp = (work_out.getTime() - work_in.getTime()) / (60 * 60 * 1000);
			wtime_count = wtime_count + time_tmp;
			break_time = break_time + str.integer("break_time");
			i++;
		}

		// Calculate work time count

		String count = Integer.toString(i - 1);
		String t_count = String.valueOf(wtime_count - break_time);
		form.setField("total_work", count);
		form.setField("total_hours", t_count);

		os.flush();
		stamper.close();
		reader.close();
	}

}
