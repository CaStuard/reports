package com.onboarding.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.exp;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.onboarding.report.templates.ReportTemplates;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.Exporters;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.RectangleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class Report {
	private static final String DATABASE_DRIVER = "org.postgresql.Driver";
	private static final String DATABASE_URL = "jdbc:postgresql://NI-NTB-010:5432/onboarding";
	private static final String DATABASE_USER = "postgres";
	private static final String DATABASE_PASSWORD = "postgres";

	private static final String PDF_DATE_FORMAT = "yyyy-MM-dd HH mm ss zzz";
	private static final String SHOW_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss zzz";

	private static final String LOGO_FILE_PATH = "src/resources/logo.png";

	private static final String REPORT_NAME = "On-boarding Progress Report ";
	private static final String REPORT_FILE_PATH = "src/resources/reports/";

	private static final String REPORT_QUERY = "select concat(participant.name, ' ',"
			+ " participant.lastname) as participantname,program.description as"
			+ " programdescription, to_char(program.started, 'yyyy/mm/dd') as startdate,"
			+ " program.status as programstatus, (select count(task.id) from task where"
			+ " task.status = 'ASIGNED' and task.program_id = program.id) as asignedtasks,"
			+ " (select count(task.id) from task where task.status = 'STARTED' and task.program_id"
			+ " = program.id) as startedtasks, (select count(task.id) from task where task.status"
			+ " = 'IN_PROGRESS' and task.program_id = program.id) as inprogresstasks, (select"
			+ " count(task.id) from task where task.status = 'COMPLETED' and task.program_id ="
			+ " program.id) as completedtasks, count(task.id) as totaltasks from participant join"
			+ " program on participant.id = program.participant_id left join task on program.id ="
			+ " task.program_id group by participantname, programdescription, programstatus,"
			+ " startdate, program.id";

	private Connection connection;
	private Logger logger;

	public Report() {
		setLogger(LogManager.getLogger(Report.class.getName()));
		try {
			Class.forName(DATABASE_DRIVER);
			setConnection(DriverManager.getConnection(DATABASE_URL, DATABASE_USER,
					DATABASE_PASSWORD));
		} catch (SQLException e) {
			getLogger().error("There was an error with the SQL", e);
		} catch (ClassNotFoundException e) {
			getLogger().error("Class was not found", e);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(final Connection givenConnection) {
		connection = givenConnection;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(final Logger givenLogger) {
		this.logger = givenLogger;
	}

	public void generateReport(final boolean preview, final String order) {
		String pdfDate = new SimpleDateFormat(PDF_DATE_FORMAT).format(new Date());
		String showDate = new SimpleDateFormat(SHOW_DATE_FORMAT).format(new Date());

		StyleBuilder style = stl.style().setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setRadius(10).setBackgroundColor(new Color(226, 228, 255))
				.setLinePen(stl.pen().setLineColor(Color.WHITE));

		RectangleBuilder background = cmp.rectangle().setStyle(style)
				.setPrintWhenExpression(exp.printInOddRow());

		try {
			JasperReportBuilder report = report()
					.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
					.setColumnStyle(ReportTemplates.columnStyle)
					.setColumnTitleStyle(ReportTemplates.boldCenteredStyle)
					.setColumnHeaderBackgroundComponent(background)
					.setDetailBackgroundComponent(background)
					.columns(
							col.column("Participant Name", "participantname", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Program Description", "programdescription",
									type.stringType()).setHorizontalAlignment(
									HorizontalAlignment.CENTER),
							col.column("Start Date", "startdate", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Status", "programstatus", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Asigned Tasks", "asignedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Started Tasks", "startedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Tasks in Progress", "inprogresstasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Completed Tasks", "completedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Total Tasks", "totaltasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER))
					.title(cmp.verticalList().add(
							cmp.image(LOGO_FILE_PATH)
									.setHorizontalAlignment(HorizontalAlignment.CENTER)
									.setDimension(50, 50),
							Components
									.text(REPORT_NAME + showDate)
									.setStyle(
											ReportTemplates.bold18CenteredStyle.setForegroundColor(
													new Color(58, 148, 210)).setBackgroundColor(
													new Color(255, 255, 255)))
									.setHorizontalAlignment(HorizontalAlignment.CENTER)
									.setHeight(35))).pageFooter(ReportTemplates.footerComponent)
					.setDataSource(REPORT_QUERY.concat(" order by ").concat(order), connection);
			if (preview) {
				report.show(false);
			} else {
				report.toPdf(Exporters.pdfExporter(new File(REPORT_FILE_PATH + REPORT_NAME
						+ pdfDate + ".pdf")));
			}
		} catch (DRException e) {
			getLogger().error("There was an error while generating the report", e);
		}
	}
}