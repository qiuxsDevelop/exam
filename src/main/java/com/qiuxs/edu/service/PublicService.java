package com.qiuxs.edu.service;

import java.util.*;

import javax.annotation.Resource;

import com.qiuxs.edu.dao.*;
import com.qiuxs.edu.entity.*;
import com.qiuxs.edu.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qiuxs.edu.util.MyUtil;

@Service
@Transactional
public class PublicService extends BaseService implements IPublicService{
	@Resource(name="examBatchDao")
	private ExamBatchDao examBatchDao;

	@Resource(name="gradeDao")
	private GradeDao gradeDao;

	@Resource(name="studentDao")
	private StudentDao studentDao;

	@Resource(name="examScoreDao")
	private ExamScoreDao examScoreDao;

	@Resource(name="adminclassDao")
	private AdminclassDao adminclassDao;

	@Resource(name="courseDao")
	private CourseDao courseDao;

	public void saveGrade(Grade grade) {
		List<Object> params = new ArrayList<Object>();
		params.add(grade.getName());
		String hql = "from Grade where name = ?";
		if (Strings.isNotEmpty(grade.getId())){
			hql += " and id <> ?";
			params.add(grade.getId());
		}
		Grade exist = gradeDao.findOneByHql(hql,params);
		if (exist!=null) {
			throw new RuntimeException("添加失败，该名称已存在！");
		}

		if (Strings.isEmpty(grade.getId())) {//新建
			grade.setId(MyUtil.getUUID());
			gradeDao.save(grade);
		}else {
			gradeDao.update(grade);
		}

	}

	public void deleteGrade(String gradeId) {
		List<Object> params = new ArrayList<Object>();
		params.add(gradeId);
		Grade exist = gradeDao.findOneByHql("from Grade where id = ?",params);
		if (exist==null) {
			throw new RuntimeException("删除失败，该年级不存在！");
		}
		List<Adminclass> adminclasses = adminclassDao.findListByHql("from Adminclass where grade.id = ?",params);
		if (adminclasses.size()>0) {
			throw new RuntimeException("删除失败，该年级下有班级信息！");
		}
		examBatchDao.update("delete from Grade where id=?",params);
	}



	public void saveAdminclass(Adminclass adminclass) {

		List<Object> params = new ArrayList<Object>();
		params.add(adminclass.getName());
		params.add(adminclass.getGrade().getId());
		String hql = "from Adminclass where name = ? and grade.id = ?";
		if (Strings.isNotEmpty(adminclass.getId())){
			hql += " and id <> ?";
			params.add(adminclass.getId());
		}
		Adminclass exist = adminclassDao.findOneByHql(hql,params);
		if (exist!=null) {
			throw new RuntimeException("添加失败，当前年级下，该名称已存在！");
		}

		if (Strings.isEmpty(adminclass.getId())) {//新建
			adminclass.setId(MyUtil.getUUID());
			hibernateDao.save(adminclass);
		}else {
			hibernateDao.update(adminclass);
		}

	}

	public void deleteAdminclass(String id) {
		List<Object> params = new ArrayList<Object>();
		params.add(id);
		Adminclass exist = (Adminclass) hibernateDao.findById(Adminclass.class,id);
		if (exist==null) {
			throw new RuntimeException("删除失败，该班级不存在！");
		}
		List<Student> adminclasses = studentDao.findListByHql("from Student where adminclass.id = ?",params);
		if (adminclasses.size()>0) {
			throw new RuntimeException("删除失败，该班级下有学生信息！");
		}
		examBatchDao.update("delete from Adminclass where id=?",params);
	}


	public void saveStudent(Student student) {

		List<Object> params = new ArrayList<Object>();
		params.add(student.getName());
		params.add(student.getAdminclass().getId());
		String hql = "from Student where name = ? and adminclass.id = ?";
		if (Strings.isNotEmpty(student.getId())){
			hql += " and id <> ?";
			params.add(student.getId());
		}
		Student exist = studentDao.findOneByHql(hql,params);
		if (exist!=null) {
			throw new RuntimeException("添加失败，当前班级下，该学生已存在！");
		}

		if (Strings.isEmpty(student.getId())) {//新建
			student.setId(MyUtil.getUUID());
			hibernateDao.save(student);
		}else {
			hibernateDao.update(student);
		}

	}

	public void deleteStudent(String id) {

		List<Object> params = new ArrayList<Object>();
		params.add(id);
		Student exist = (Student) hibernateDao.findById(Student.class,id);
		if (exist==null) {
			throw new RuntimeException("删除失败，该学生不存在！");
		}
		List<ExamScore> adminclasses = examScoreDao.findListByHql("from ExamScore where student.id = ?",params);
		if (adminclasses.size()>0) {
			throw new RuntimeException("删除失败，该学生有考试信息！");
		}
		hibernateDao.delete(exist);
	}


	public void saveExamBatch(ExamBatch exam) {
		String hql = "from ExamBatch where name = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(exam.getName());
		if (Strings.isNotEmpty(exam.getId())){
			hql += " and id <> ?";
			params.add(exam.getId());
		}
		ExamBatch exist = examBatchDao.findOneByHql(hql,params);
		if (exist!=null) {
			throw new RuntimeException("添加失败，该考次名称已存在！");
		}
		if (Strings.isEmpty(exam.getId())) {//新建
			exam.setId(MyUtil.getUUID());
			examBatchDao.save(exam);
		}else{
			examBatchDao.update(exam);
		}

	}

	public void deleteExamBatch(String id) {
		List<Object> params = new ArrayList<Object>();
		params.add(id);
		ExamBatch exist = examBatchDao.findOneByHql("from ExamBatch where id = ?",params);
		if (exist==null) {
			throw new RuntimeException("删除失败，该考次不存在！");
		}
		List<ExamScore> score = examScoreDao.findListByHql("from ExamScore where examBatch.id = ?",params);
		if (score.size()>0) {
			throw new RuntimeException("删除失败，该考次下有成绩信息！");
		}
		hibernateDao.delete(exist);
	}


	public int uplaodAdminclass(List<List<String>> datas, String gradeId) {

		int success = 0;

		try {
			Grade grade = (Grade) hibernateDao.findById(Grade.class,gradeId);
			if (grade==null) {
				throw new RuntimeException("该年级不存在！");
			}

			int index = -1;
			List<String> rowList = datas.get(0);
			for (int i = 0; i < rowList.size(); i++) {
				if ("班级".equals(rowList.get(i).trim())){
					index = i;
					break;
				}
			}
			if (index == -1){
				throw new RuntimeException("第一列没有找到[班级]字段");
			}

			//该年级下的班级信息
			List<Adminclass> existList = adminclassDao.findListByHql("from Adminclass where grade.id = ?",new Object[]{gradeId});

			List<String> existNames = new ArrayList<String>();
			for (Adminclass adminclass : existList) {
				existNames.add(adminclass.getName());
			}

			//新建
			for (int i = 1; i < datas.size(); i++) {
				String name = datas.get(i).get(index);
				if (!existNames.contains(name)){
					Adminclass adminclass = new Adminclass();
					adminclass.setId(MyUtil.getUUID());
					adminclass.setGrade(grade);
					adminclass.setName(name);
					try {
						adminclass.setOrder(Integer.parseInt(name));
					} catch (NumberFormatException e) {
						adminclass.setOrder(0);
					}
					hibernateDao.save(adminclass);
					success ++;
					existNames.add(name);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public int uplaodStudent(List<List<String>> datas, String gradeId) {

		int success = 0;

		Grade grade = (Grade) hibernateDao.findById(Grade.class,gradeId);
		if (grade==null) {
			throw new RuntimeException("该年级不存在！");
		}

		int classIndex = -1;

		int nameIndex = -1;

		List<String> rowList = datas.get(0);
		for (int i = 0; i < rowList.size(); i++) {
			if ("班级".equals(rowList.get(i).trim())){
				classIndex = i;
				continue;
			}
			if ("姓名".equals(rowList.get(i).trim())){
				nameIndex = i;
				continue;
			}
		}
		if (classIndex == -1){
			throw new RuntimeException("第一列没有找到[班级]字段");
		}
		if (nameIndex == -1){
			throw new RuntimeException("第一列没有找到[姓名]字段");
		}

		//该年级下的学生信息
		List<Student> existList = studentDao.findListByHql("from Student where adminclass.grade.id = ?",new Object[]{gradeId});
		List<String> exists = new ArrayList<String>();
		for (Student student : existList) {
			exists.add(student.getName()+"-"+student.getAdminclass().getName());
		}
		//该年级下的班级信息
		List<Adminclass> adminclasses = adminclassDao.findListByHql("from Adminclass where grade.id = ?",new Object[]{gradeId});
		Map<String,Adminclass> adminclassMap = new HashMap<String, Adminclass>();
		for (Adminclass adminclass : adminclasses) {
			adminclassMap.put(adminclass.getName(),adminclass);
		}

		//新建
		for (int i = 1; i < datas.size(); i++) {
			String name = datas.get(i).get(nameIndex);
			String adminclassName = datas.get(i).get(classIndex);
			String key = name + "-" + adminclassName;
			if (!exists.contains(key)){
				Student student = new Student();
				student.setId(MyUtil.getUUID());
				Adminclass adminclass = adminclassMap.get(adminclassName);
				if (adminclass == null ){
					throw new RuntimeException("第 "+i+" 行，班级["+adminclassName+"]不存在");
				}
				student.setAdminclass(adminclass);
				student.setName(name);
				hibernateDao.save(student);
				exists.add(key);
				success ++;
			}
		}
		return success;
	}


	public void saveCourse(Course course) {
		String hql = "from Course where name = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(course.getName());
		if (Strings.isNotEmpty(course.getId())){
			hql += " and id <> ?";
			params.add(course.getId());
		}
		Course exist = courseDao.findOneByHql(hql,params);
		if (exist!=null) {
			throw new RuntimeException("添加失败，该课程已存在！");
		}
		if (Strings.isEmpty(course.getId())) {//新建
			course.setId(MyUtil.getUUID());
			hibernateDao.save(course);
		}else{
			hibernateDao.update(course);
		}
	}

	public void deleteCourse(String id) {

		List<Object> params = new ArrayList<Object>();
		params.add(id);
		Course exist = courseDao.findOneByHql("from Course where id = ?",params);
		if (exist==null) {
			throw new RuntimeException("删除失败，该课程不存在！");
		}
		List<ScoreItem> score = hibernateDao.findListByHql("from ScoreItem where course.id = ?",params);
		if (score.size()>0) {
			throw new RuntimeException("删除失败，该课程下有成绩信息！");
		}
		hibernateDao.delete(exist);

	}
}
