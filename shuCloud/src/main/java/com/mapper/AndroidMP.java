package com.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.svo.FolderVO;
import com.android.svo.ParamsVO;
import com.android.svo.FileVO;
import com.android.svo.UserVO;

public interface AndroidMP {
	
	//TESTER
	//public Map getUser(HashMap<String, String> user);
	//public int insertUser(Map paramMap);
	
	//SELECT
	public Map userCheck(UserVO vo);
	public Map userCheckAsId(String userId);
	public Map emailCheck(String userEmail);
	public Map getInitFolder(FolderVO vo);
	public int checkDupleFolder(FolderVO vo);
	public int progressState(ParamsVO vo);
	public List checkFilesInFolder(Map ParamMap);
	public Map checkUserInFolder(FolderVO vo);
	public ArrayList<FolderVO> getUserFolders(UserVO vo);
	public ArrayList<FileVO> getFolderFiles(FolderVO vo);
	public ArrayList<UserVO> getFolderUsers(FolderVO vo);
	
	// INSERT
	public int insertUser(UserVO vo);
	public int insertFolder(FolderVO vo);
	public int insertAdditionalUserFolder(FolderVO vo);
	public int insertFile(FileVO vo);
	public int insertBundleFiles(ArrayList<FileVO> bundleVo);
	
	//UPDATE
	public int updateFolderDealState(FolderVO vo);
	public int updateFolderProgress(FolderVO vo);
	public int updateFileDealState(ParamsVO vo);
	public int updateDeleteFile(ArrayList<FileVO> bundleVo);
	public int setupLeaderFolder(FolderVO vo);
	public int setupPersonalFolder(FolderVO vo);
	public int updateFolderProfile(FolderVO vo);
	public int updateFolderName(FolderVO vo);
	
	//DELETE
	public int deleteUserFolder(FolderVO vo);
	

}
