package team.capstonelongstone.freetraveler.post.board;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import team.capstonelongstone.freetraveler.account.domain.Account;
import team.capstonelongstone.freetraveler.good.GoodService;
import team.capstonelongstone.freetraveler.good.domain.Good;
import team.capstonelongstone.freetraveler.pick.PickRepository;
import team.capstonelongstone.freetraveler.pick.domain.Pick;
import team.capstonelongstone.freetraveler.post.board.dto.BoardDto;
import team.capstonelongstone.freetraveler.post.board.dto.PostListDTO;
import team.capstonelongstone.freetraveler.post.img.ImgService;
import team.capstonelongstone.freetraveler.post.place.PlaceRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * @author 정순범, 박성호
 * 보드 생성 및 저장
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PlaceRepository placeRepository;
    private final ImgService imgService;
    private final GoodService goodService;
    private final PickRepository pickRepository;

    /**
     * 보드 생성 및 이미지 저장
     */
    public Board generateBoard(HttpServletRequest request, @RequestParam("repImg")MultipartFile file) throws IOException {

        HttpSession session=request.getSession();
        Account account = (Account) session.getAttribute("account");

        String postName = request.getParameter("postName");
        Integer totalDays = Integer.valueOf(request.getParameter("totalDays"));
        String comment = request.getParameter("comment");
        String totalTrans=request.getParameter("totalTrans");

        Account author = account;
        int sumTotalCost = 0;

        for (int day=0;day<totalDays;day++) {
            String varPlength = day + "_plength";
            Integer _plength = Integer.valueOf(request.getParameter(varPlength));
            for (int j = 0; j < _plength; j++) {
                Integer cost = Integer.valueOf(request.getParameter(day + "_" + j + "_" + "cost"));
                sumTotalCost+=cost;
            }
        }

        List<String> list = imgService.boardSaveImg(request, file);

        Board board = Board.builder().postName(postName).totalDays(totalDays).totalCost(sumTotalCost).comment(comment).goodCnt(0)
                .author(author).totalTrans(totalTrans).repImgPath(list.get(0)).repImgName(list.get(1)).build();
        saveBoard(board);
        return board;
    }

    public void modifyBoard(Long id,HttpServletRequest request, @RequestParam("repImg")MultipartFile file) throws IOException {

        HttpSession session=request.getSession();
        Board accountByBoard = boardRepository.findById(id).orElse(null);
        Account account = Objects.requireNonNull(accountByBoard).getAuthor();

        String postName = request.getParameter("postName");
        Integer totalDays = Integer.valueOf(request.getParameter("totalDays"));
        String comment = request.getParameter("comment");
        String totalTrans=request.getParameter("totalTrans");

        Account author = account;
        int sumTotalCost = 0;

        for (int day=0;day<totalDays;day++) {
            String varPlength = day + "_plength";
            Integer _plength = Integer.valueOf(request.getParameter(varPlength));
            for (int j = 0; j < _plength; j++) {
                Integer cost = Integer.valueOf(request.getParameter(day + "_" + j + "_" + "cost"));
                sumTotalCost+=cost;
            }
        }

        if(Objects.isNull(file)) {
            List<String>list = imgService.boardModifyImg(id, request);
            int finalSumTotalCost = sumTotalCost;
            boardRepository.findById(id).ifPresent(target -> {
                BoardDto boardDto = BoardDto.builder().postName(postName).totalDays(totalDays).totalCost(finalSumTotalCost).comment(comment).goodCnt(0)
                        .author(author).totalTrans(totalTrans).repImgPath(list.get(0)).repImgName(list.get(1)).build();

                Board targetBoard = boardDto.toEntity(id);
                boardRepository.save(targetBoard);
            });
        }else{
//            List<String>list = imgService.boardSaveImg(request, file);
            List<String>list = imgService.boardModifyImg(id, file);
            int finalSumTotalCost = sumTotalCost;
            boardRepository.findById(id).ifPresent(target -> {
                BoardDto boardDto = BoardDto.builder().postName(postName).totalDays(totalDays).totalCost(finalSumTotalCost).comment(comment).goodCnt(0)
                        .author(author).totalTrans(totalTrans).repImgPath(list.get(0)).repImgName(list.get(1)).build();

                Board targetBoard = boardDto.toEntity(id);
                boardRepository.save(targetBoard);
            });
        }

    }

    /**
     * 보드 저장
     */
    public void saveBoard(Board target) {
        boardRepository.save(target);
    }

    /**
     * 게시글 조회 리스트 출력
     */
    public String getPostList(PostListDTO postListDTO,HttpServletRequest request) throws JSONException {

        if(Objects.isNull(postListDTO.getSearch())){
            postListDTO.setSearch("");
        }

        String pick="";
        if(postListDTO.getIsMyPick().equals("") ||postListDTO.getIsMyPick().equals("all" )){
            pick="all";
        } else if (postListDTO.getIsMyPick().equals("pick")){
            pick="true";
        }else{
            pick="false";
        }

        Sort sort = null;
        if(postListDTO.getOrderBy().equals("asc")|| postListDTO.getOrderBy().equals("")) {
            if(postListDTO.getSort().equals("recent") || postListDTO.getSort().equals("")) {
                sort = Sort.by(Sort.Direction.ASC, "createdDate");
            }
            else {
                sort = Sort.by(Sort.Direction.ASC, "goodCnt");
            }
        }
        else{ //desc
            if(postListDTO.getSort().equals("recent") || postListDTO.getSort().equals("")){
                sort = Sort.by(Sort.Direction.DESC, "createdDate");
            }
            else {
                sort = Sort.by(Sort.Direction.DESC, "goodCnt");
            }

        }

        HttpSession session=request.getSession();
        Account account = (Account) session.getAttribute("account");

        Pageable pageable= PageRequest.of(postListDTO.getPage(), postListDTO.getPageSize(),sort);

        Page<Board> all=null;
        //pick all 이면서 title일 때

        if(postListDTO.getSort().equals("random")){
            all=boardRepository.findAllPickByRand(pageable);
        }
        else{
            if(pick.equals("all") && (postListDTO.getMethod().equals("") || postListDTO.getMethod().equals("title"))){
                all = boardRepository.findAllPickAllByTitle(pageable,postListDTO.getSearch());
            }else if(pick.equals("all") && postListDTO.getMethod().equals("author")){
                all = boardRepository.findAllPickAllByAuthor(pageable,postListDTO.getSearch());
            }
            else if(postListDTO.getMethod().equals("") || postListDTO.getMethod().equals("title")){
                all = boardRepository.findAllPickByTitle(pageable, account.getId(), postListDTO.getSearch(), pick);
            }else{
                all = boardRepository.findAllPickByAuthor(pageable, account.getId(), postListDTO.getSearch(), pick);
            }
        }

        if(postListDTO.getIsMine().equals("true")){
            if(postListDTO.getOrderBy().equals("asc")) {
                System.out.println("asc");
                sort = Sort.by(Sort.Direction.ASC, "createdDate");
            }else{
                sort = Sort.by(Sort.Direction.DESC, "createdDate");
            }
            Pageable pageableMine= PageRequest.of(postListDTO.getPage(), postListDTO.getPageSize(),sort);
            all = boardRepository.findAllIsMine(pageableMine, account.getId());
        }

        if(!postListDTO.getFriend().isEmpty()){ //친구 아이디 들어올 때
            all=boardRepository.findAllByFriend(pageable,postListDTO.getFriend(),postListDTO.getSearch());
        }

        int boardSize=0;
        int max=0;
        if(postListDTO.getSearch().isEmpty()){
            List<Board> allBoardSize = boardRepository.findAll();
            boardSize= allBoardSize.size();
            max= (int) Math.ceil((float)boardSize/(float)postListDTO.getPageSize())-1;
        }else{
            max=all.getTotalPages()-1;
        }
        if(max==-1){
            max=0;
        }

        //json
        JSONObject page=new JSONObject();
        page.put("page",postListDTO.getPage());
        page.put("max",max);
        page.put("pageSize",postListDTO.getPageSize());
        page.put("totalPost",boardSize);

        JSONArray array=new JSONArray();
        for (Board board : all) {

            Good good = goodService.returnGoodStatus(account, board);
            Pick byUserAndBoard = pickRepository.findByUserAndBoard(account, board);

            String pickStatus="";
            if(Objects.isNull(byUserAndBoard)){
                pickStatus="false";
            }else{
                pickStatus=byUserAndBoard.getPickStatus();
            }

            String goodStatus="";
            if(Objects.isNull(good)){
                goodStatus="false";
            }else{
                goodStatus="true";
            }

            JSONObject post = new JSONObject();
            post.put("id",board.getId());
            post.put("author",board.getAuthor().getUserId());
            post.put("repimg",board.getRepImgName());
            post.put("postName",board.getPostName());
            post.put("totalCost",board.getTotalCost());
            post.put("totalDays",board.getTotalDays());
            post.put("totalTrans",board.getTotalTrans());
            post.put("comment",board.getComment());
            post.put("good",board.getGoodCnt());
            post.put("isGood",goodStatus);
            post.put("isPick",pickStatus);
            array.put(post);
            page.put("post",array);
        }

        return page.toString();
    }

    public Board getBoard(Long id){
        return boardRepository.findById(id).orElse(null);
    }
}
