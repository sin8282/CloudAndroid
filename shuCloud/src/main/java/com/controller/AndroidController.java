package com.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.android.svo.FileVO;
import com.android.svo.FolderVO;

import com.android.svo.ParamsVO;
import com.android.svo.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapper.AndroidMP;

@Controller
public class AndroidController {
	
	private static final String ROOT_DIR = "F:/";
	private static final String SERVER_DIR = "androidUpload/";
	private static final String PROFILE_DIR = "profile/";
	private static final String S = "/";
	//private final int DELAY_TIME = 1; // GIF다음 프레임으로 전환되는 지연 시간(ms)
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Resource
	AndroidMP androidMp;
	
	
	/* USER 관련
	 * USER 등록 및 조회 
	 */
	@ResponseBody 
	@RequestMapping("/userCheck")
	public Map userCheck(@RequestBody UserVO searchVo) {
			String USER_DIR = searchVo.getUserId();
			File file = new File(ROOT_DIR+SERVER_DIR+USER_DIR);
			Map result = null;
		try {
			result = androidMp.userCheck(searchVo);
			if (result == null) {
				System.out.println("회원가입을 진행합니다. 및 폴더 생성을 진행합니다.");
				androidMp.insertUser(searchVo);
				result = androidMp.userCheck(searchVo);
				if (file.exists() && file.isDirectory()) throw new Exception("이미 존재하는 폴더입니다."); 
				file.mkdir();
			}else if (result.containsKey("userId") && result.containsKey("userEmail") && result.containsKey("name")) {
				result.put("msg",true);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			result = new HashMap();
			result.put("msg",false);
		}
		
		
		return result;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}	
	@ResponseBody 
	@RequestMapping("/userCheckAsId")
	public Map userCheckAsId(@RequestParam String userId) {

		Map result = new HashMap();
		try {
			result = androidMp.userCheckAsId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg",false);
		}
		return result;
	}
	@ResponseBody 
	@RequestMapping("/getFolderUsers")
	public ArrayList<UserVO> getFolderUsers(@RequestBody FolderVO folderVo) {
		ArrayList<UserVO> result = null;
		try {
			result = androidMp.getFolderUsers(folderVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/* FILE 관련
	 * 폴더 처리현황 업데이트 ,FILE 등록, 조회, 삭제
	 */
	@ResponseBody
	@RequestMapping("/folderDealState")
	public String folderDealState(@RequestBody FolderVO folderVo) {
		int result = 0 ;
		try {
			result = androidMp.updateFolderDealState(folderVo);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result == 1 ? "success" : "fail";
	}
	
	@ResponseBody
	@RequestMapping("/uploadFile")
	public String uploadFile(ParamsVO paramsVo, MultipartHttpServletRequest request) {
		String result = "ERR NONE";
		String USER_DIR = paramsVo.getFolderOwner()+S+paramsVo.getFolderName()+S;
		int totalCnt =  paramsVo.getTotalCnt();
		String percent = "0";
		FolderVO folderVo = new FolderVO();
		ArrayList<FileVO> bundleVo = new ArrayList<FileVO>();
		
		/*
		 * System.out.println(paramVo.getPresentCnt());
		 * System.out.println(paramVo.getTotalCnt());
		 * System.out.println(paramVo.getFolderName());
		 * System.out.println(paramVo.getUserId());
		 */

		try {
			folderVo.setDealState("N");
			folderVo.setSeqFolder(paramsVo.getSeqFolder());
			Map<String, MultipartFile> files =  request.getFileMap();
			Iterator<String> keys = files.keySet().iterator();
			MultipartFile fileStream = null;
			
	        while( keys.hasNext() ){
		
				String key = keys.next(); 
				fileStream = files.get(key);
				
				String contentType= fileStream.getContentType();
				Path copyOfLocation = Paths.get(ROOT_DIR+SERVER_DIR+USER_DIR+fileStream.getName());
			    Files.copy(fileStream.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
				
				/* Files.copy가 성능이 월등히 좋은거 같음 그냥 다바꾸는 걸로 
				String contentType= fileStream.getContentType().replace("image/", "");
				File outputFile = new File(ROOT_DIR+SERVER_DIR+USER_DIR+fileStream.getName());
				
				switch (contentType) {
				case "jpeg":
				case "png":
				case "webp":
					BufferedImage image = ImageIO.read(fileStream.getInputStream());
					ImageIO.write(image,contentType,outputFile); 
					break;
				case "gif":
					GifDecoder gifDecoder = new GifDecoder();
					gifDecoder.read(fileStream.getInputStream());
					
					FileImageOutputStream fileImageOutputStream 
						= new FileImageOutputStream(outputFile);
					
					GifSequenceWriter gifSequenceWriter = new GifSequenceWriter(
							fileImageOutputStream
							, gifDecoder.getFrame(0).getType()
							, DELAY_TIME
							, true
					);
					BufferedImage imageGif = gifDecoder.getFrame(0); // 인덱스에 해당하는 프레임 추출
					gifSequenceWriter.writeToSequence(imageGif);
					
		            for(int i=1; i<gifDecoder.getFrameCount()-1; i++) {
		                BufferedImage nextImage = gifDecoder.getFrame(i);
		                gifSequenceWriter.writeToSequence(nextImage);
		            }
		            fileImageOutputStream.flush();
		            gifSequenceWriter.close();
					break;
				case "video/mp4":
					Path copyOfLocation = Paths.get(ROOT_DIR+SERVER_DIR+USER_DIR+fileStream.getName());
				    Files.copy(fileStream.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);// copy의 옵션은 기존에 존재하면 REPLACE(대체한다), 오버라이딩 한다
				    break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + contentType);
				}
				*/
				
				FileVO fileVo = new FileVO();
				fileVo.setSeqFolder(paramsVo.getSeqFolder());
				fileVo.setFileOwner(paramsVo.getUserId());
				fileVo.setFileUploadedName(fileStream.getName());
				fileVo.setFileOriginalName(fileStream.getOriginalFilename());
				fileVo.setFilePath(SERVER_DIR+USER_DIR);
				fileVo.setFileSize(fileStream.getSize()+"");
				fileVo.setFileType(contentType);
				
				//androidMp.insertFile(fileVo);
				bundleVo.add(fileVo);
				result = "success";
	        }
	        androidMp.insertBundleFiles(bundleVo);
	        percent = progress(paramsVo,totalCnt);
			folderVo.setProgressPercentage(percent);
			androidMp.updateFolderProgress(folderVo);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(Integer.parseInt(percent) >= 100 ) {
				folderVo.setDealState("P");
				folderVo.setProgressPercentage("0");
				androidMp.updateFolderDealState(folderVo);
				androidMp.updateFileDealState(paramsVo);
				System.out.println("완료");
			}
		} 
		
		return result;
		
	}
	
	private String progress(ParamsVO vo,int totalCnt) {
		double dealCnt = androidMp.progressState(vo);
		double percent = (dealCnt / totalCnt) * 100;
		String result = String.valueOf(Math.round(percent));
		//System.out.println("퍼센트"+result);
		return  result;
	}
	
	@ResponseBody
	@RequestMapping("/getFolderFiles")
	public ArrayList<FileVO> getFolderFiles(@RequestBody FolderVO searchVo){
		ArrayList<FileVO> result =androidMp.getFolderFiles(searchVo);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/deleteFile")
	public String deleteFile(@RequestBody ArrayList<FileVO> Files) {
		int result = 0 ;
		try {
			result = androidMp.updateDeleteFile(Files);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result+"";
	}
	
	@ResponseBody
	@RequestMapping("/uploadFolderProfile")
	public String uploadFolderProfile(ParamsVO paramsVo, MultipartHttpServletRequest request) {
		String result = "ERR NONE";

		try {

			Map<String, MultipartFile> files =  request.getFileMap();
			Iterator<String> keys = files.keySet().iterator();
			MultipartFile fileStream = null;
			
	        while( keys.hasNext() ){
		
				String key = keys.next(); 
				fileStream = files.get(key);
				
				String contentType= fileStream.getContentType();
				if(contentType.indexOf("jpeg") >= 0 ) {
					FolderVO vo = new FolderVO();
					Path location = Paths.get(ROOT_DIR+SERVER_DIR+PROFILE_DIR+fileStream.getName());
					vo.setSeqFolder(paramsVo.getSeqFolder());
					vo.setUserCustomProfile(SERVER_DIR+PROFILE_DIR+fileStream.getName());
					Files.copy(fileStream.getInputStream(), location, StandardCopyOption.REPLACE_EXISTING);
					androidMp.updateFolderProfile(vo);
					result = "success";
				}
	        }
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
		
	}
	@ResponseBody
	@RequestMapping("/uploadFolderName")
	public String uploadFolderName(@RequestBody FolderVO vo) {
		String result = "ERR NONE";
		try {
			androidMp.updateFolderName(vo);
			result = "success";
		} catch (Exception e) {
			result = e.getMessage();
			e.printStackTrace();
		}
		return result;
	}
		
		
	
	
	/* Folder 관련
	 * Folder 등록, 조회, 옵션변경
	 */
	@ResponseBody
	@RequestMapping("/getInitFolder")
	public Map getInitFolder(@RequestBody FolderVO vo) {
		return androidMp.getInitFolder(vo);
	}
	@ResponseBody
	@RequestMapping("/addFolder")
	public ArrayList<FolderVO> addFolder(@RequestBody FolderVO vo) {
	
		ArrayList<FolderVO> result = new ArrayList<FolderVO>();
		UserVO userVo = new UserVO();
		vo.setFolderPath(SERVER_DIR+vo.getFolderOwner());
		try {
			checkMkdir(vo);
			userVo.setUserId(vo.getFolderOwner());
			result = androidMp.getUserFolders(userVo);
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			FolderVO msg = new FolderVO();
			msg.setMsg(e.getMessage());
			result.add(msg);
		} catch (Exception e) {
			FolderVO msg = new FolderVO();
			msg.setMsg(e.getMessage());
			result.add(msg);
		}
		
		return result;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}
	
	@ResponseBody
	@RequestMapping("/getUserFolders")
	public ArrayList<FolderVO> getUserFolders(@RequestBody UserVO vo, HttpServletRequest request, HttpServletResponse response) {
		
		ArrayList<FolderVO> list = null;
		list = androidMp.getUserFolders(vo);
		
		return list;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}
	
	private void checkMkdir(FolderVO vo) throws Exception {
		System.out.println(ROOT_DIR+vo.getFolderPath()+S+vo.getFolderName());
		File file = new File(ROOT_DIR+vo.getFolderPath()+S+vo.getFolderName());
		
		if (androidMp.checkDupleFolder(vo) > 0) throw new Exception("이미 존재하는 폴더입니다. (ERR1)");
		else if (file.exists() && file.isDirectory()) throw new Exception("이미 존재하는 폴더입니다. (ERR2)"); 
		
		androidMp.insertFolder(vo);
		if (file.mkdir()) System.out.println("성공");
		else System.out.println("성공");
	
	}
	/* Description 
	 * 방장이 사용자 추가할때*/
	@ResponseBody
	@RequestMapping("/additionalUserFolders")
	public String additionalUserFolders(@RequestBody ArrayList<FolderVO> listVo, HttpServletRequest request, HttpServletResponse response)  {

		String result = "ERR NONE";
		String userEmail = request.getParameter("userEmail");
		int failCnt = 0; 
		
		try {
			Map userInfo = androidMp.emailCheck(userEmail);
			if(userInfo == null) throw new Exception("존재하지 않는 사용자입니다.");
			for (FolderVO vo :listVo) {
				System.out.println(userInfo.toString());
				vo.setFolderUsers((String) userInfo.get("userId"));
				if (androidMp.checkDupleFolder(vo) > 0) {
					failCnt++;
				}else {
					androidMp.insertAdditionalUserFolder(vo);	
				}
			}
			
			if(failCnt == listVo.size()) {
				throw new Exception("이미 상대방에게 공유 요청 됐습니다. (CASE3)");
			}else if (failCnt > 0) {
				throw new Exception("일부 상대방에게 없는 폴더만 공유 요청 됐습니다. (CASE4)");
			}
			else {
				result = "성공적으로 상대방에게 요청되었습니다.";
			}
		} catch (Exception e) {
			result = e.getMessage();
			e.printStackTrace();
		}
		return result;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}
	/* Description 
	 * 추가했을때 유저가 동의를 해야 최종적으로 본인 리스트에 증록이 됨*/
	@ResponseBody
	@RequestMapping("/addedUserConfirm")
	public String addedUserConfirm(@RequestBody FolderVO folderVo, @RequestParam boolean confirm) {
		String result = "ERR NONE";
		try {
			if (confirm) {
				folderVo.setDealState("P");
				folderVo.setProgressPercentage("0");
				androidMp.updateFolderDealState(folderVo);
				result = "추가";
			}else {
				androidMp.deleteUserFolder(folderVo);
				result = "삭제";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/* Description 
	 * 이건 위에 꺼랑 별개로 그냥 자기리스트에 있는 폴더 삭제할때 */
	@ResponseBody
	@RequestMapping("/deleteFolder")
	public String deleteFolder(@RequestBody List<FolderVO> folderList,@RequestParam boolean isCheckFiles) {
		String result = "ERR NONE";
		int deleteCnt = 0;
		int resultSize = 0;
		try {
				Map<String, Object> ParamMap = new HashMap<String, Object>();
				ParamMap.put("folderUsers", folderList.get(0).getFolderUsers());
				ParamMap.put("folders", folderList);
				
				if(isCheckFiles) {
					List<String> resultList = androidMp.checkFilesInFolder(ParamMap);
					resultSize = resultList.size();
					if (resultSize > 0) {
						for(FolderVO vo : folderList){
							if(resultList.contains(vo.getSeqFolder())) {
								deleteCnt = deleteCnt + androidMp.deleteUserFolder(vo);
							}
						}					
					}
				}else {
					resultSize = 1;
					System.out.println(folderList.get(0).toString());
					deleteCnt = deleteCnt + androidMp.deleteUserFolder(folderList.get(0));
				}

				
				if(resultSize == folderList.size()) {
					result = "선택한 폴더 삭제 완료";
				}else if (deleteCnt>0){
					result = "본인자료가 없는 폴더만 삭제 완료";
				}else {
					result = "본인자료가 있어서 선택한 폴더 전부 삭제 불가";
				}
				
				for(FolderVO vo : folderList){
					if(androidMp.checkUserInFolder(vo) == null) {
						File folder = new File(ROOT_DIR+S+vo.getFolderPath()+S+vo.getFolderName());
						folder.delete();
					}
				}					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/setupFolder")
	public String setupFolder(@RequestBody FolderVO vo, @RequestParam boolean isPersonal) {
		String result = "ERROR NONE";
		try {
			if(isPersonal) androidMp.setupPersonalFolder(vo);
			else androidMp.setupLeaderFolder(vo);
			result = "success";
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	

	/* 연습용으로 사용했던거
	@ResponseBody 
	@RequestMapping("/androidLogin")
	public Map androidLogin(HttpServletRequest request, HttpServletResponse response) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("userId", request.getParameter("userId"));
		map.put("userPassword", request.getParameter("userPassword"));
		HashMap result = (HashMap) androidMp.getUser(map);
	
	
		if (result != null) {
			result.put("success", true);
		}else {
			result = new HashMap();
			result.put("success", false);
		}
			
		return result;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}
	
	@ResponseBody 
	@RequestMapping("/androidRegister")
	public Map androidRegister(@XParamMap ParamMap paramMap, HttpServletRequest request, HttpServletResponse response) {
		
		Map map = (HashMap) paramMap.getMap();
		HashMap result = new HashMap();
		
		try {
			androidMp.insertUser(map);
			result.put("success",true);
		} catch (Exception e) {
			result.put("success",false);
		}
		
		
		return result;
		
		//JSONObject jsonObject = new JSONObject(result);
		//return jsonObject;
	}
	*/
	
	
	
}
