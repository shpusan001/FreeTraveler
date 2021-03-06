package team.capstonelongstone.freetraveler.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Check;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.capstonelongstone.freetraveler.account.domain.Account;
import team.capstonelongstone.freetraveler.interceptor.CheckSession;
import team.capstonelongstone.freetraveler.post.board.Board;
import team.capstonelongstone.freetraveler.post.board.BoardRepository;
import team.capstonelongstone.freetraveler.post.board.BoardService;
import team.capstonelongstone.freetraveler.post.board.dto.PostListDTO;
import team.capstonelongstone.freetraveler.post.day.DayService;
import team.capstonelongstone.freetraveler.post.img.ImgService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author 정순범
 * 게시물 관련 서비스
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final BoardService boardService;

    private final DayService dayService;

    private final PostService postService;

    private final ImgService imgService;

    /**
     * 게시물 등록 및 수정
     */
    @CheckSession
    @PostMapping("/post") 
    @ResponseBody
    public ResponseEntity<?> generateBoard(HttpServletRequest request, @RequestParam(value = "repImg", required = false)MultipartFile file) throws JSONException, IOException {

        String requestFile = request.getParameter("repImg");
        String mode = request.getParameter("mode");
        if (mode.equals("write")) { //게시글 등록
            try {
                Board board = boardService.generateBoard(request, file);
                dayService.generateDay(null, request, board);

                HashMap<String,Integer> boardId=new HashMap<>();
                boardId.put("id",board.getId().intValue());

                return new ResponseEntity(boardId,HttpStatus.valueOf(201));
            } catch (Exception e) {
                return new ResponseEntity(HttpStatus.valueOf(409));
            }
        }
        else{ //게시글 수정
            try {
                Long targetBoardId = Long.parseLong(request.getParameter("id"));
                Board board = boardService.getBoard(targetBoardId);
                if (Objects.nonNull(board)) {
                    boardService.modifyBoard(targetBoardId, request, file);
                    dayService.generateDay(targetBoardId, request, board);

                    HashMap<String, Integer> boardId = new HashMap<>();
                    boardId.put("id", board.getId().intValue());
                    return new ResponseEntity(boardId, HttpStatus.valueOf(201));
                }else{
                    return new ResponseEntity(HttpStatus.valueOf(409));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity(HttpStatus.valueOf(409));
            }
        }
    }

    /**
     * 게시물 조회
     */

    @GetMapping("/post")
    @ResponseBody
    public ResponseEntity getPost(@RequestParam("id")String boardId,HttpServletRequest request) throws JSONException, IOException {
        String post = postService.getPost(boardId,request);
        return new ResponseEntity(post,HttpStatus.valueOf(200));
    }

    /**
     * 게시물 삭제
     */
    @CheckSession
    @DeleteMapping("/post")
    public ResponseEntity deletePost(@RequestBody HashMap<String,HashMap<String, String> >id){
        String boardId = id.get("data").get("id");
        try {
            imgService.deleteImg(boardId);
            postService.deletePost(boardId); //board 지우면 day, place 같이 지워짐
            return new ResponseEntity(HttpStatus.valueOf(201));
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.valueOf(409));
        }
    }

    @GetMapping("/deleteImg")
    public void deleteImg(@RequestBody HashMap<String,String >id){
        String boardId = id.get("id");
        imgService.deleteImg(boardId);
    }


    /**
     * 이미지 가져오기
     */

    @GetMapping(value = "/{boardImg}", produces = MediaType.IMAGE_JPEG_VALUE) //이미지 접근 링크
    public ResponseEntity<byte[]> files(@PathVariable String boardImg) throws Exception {

        String fileDir = boardImg;
        File file=new File(fileDir);
        HttpHeaders header = new HttpHeaders();

        header.add("Content-type", Files.probeContentType(file.toPath()));

        return new ResponseEntity(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
    }

    /**
     * 게시물 조회 리스트 출력
     */
    @GetMapping("/post/list")
    @ResponseBody
    public String getPostList(PostListDTO postListDTO,HttpServletRequest request) throws JSONException {
        return boardService.getPostList(postListDTO,request);
    }

}
