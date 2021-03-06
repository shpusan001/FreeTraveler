package team.capstonelongstone.freetraveler.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.capstonelongstone.freetraveler.account.AccountRepository;
import team.capstonelongstone.freetraveler.account.domain.Account;
import team.capstonelongstone.freetraveler.follow.domain.Follow;
import team.capstonelongstone.freetraveler.follow.dto.FollowResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author 정순범
 * 친구 추가, 삭제 기능
 */
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public FollowService(FollowRepository followRepository, AccountRepository accountRepository) {
        this.followRepository = followRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * 친구 추가 기능
     */
    public ResponseEntity addFollow(HttpServletRequest request, String targetId){
        HttpSession session=request.getSession();
        Account account = (Account) session.getAttribute("account");

        Account findAccount=accountRepository.findByUserId(targetId);
        if(Objects.isNull(findAccount)){ //찾는 아이디 없을때
            return new ResponseEntity("존재하지 않는 아이디 입니다.", HttpStatus.valueOf(401));
        }

        if(!isPresentTargetId(account,targetId)){
            Follow follow=new Follow();
            follow.setAccount(account);
            follow.setTargetId(targetId);

            followRepository.save(follow);
            return new ResponseEntity("친구 추가 성공", HttpStatus.valueOf(200));
        }
        else{
            System.out.println("이미 추가된 친구 입니다.");
            return new ResponseEntity("이미 추가되었거나 잘못된 입력값입니다.",HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 친구 삭제 기능
     */
    public ResponseEntity<?> deleteFollow(HttpServletRequest request, String targetId){
        HttpSession session=request.getSession();
        Account account = (Account) session.getAttribute("account");

        if(!isPresentTargetId(account,targetId)){ //존재하는 targetId가 없을때
            return new ResponseEntity("존재하지 않는 아이디 입니다.",HttpStatus.BAD_REQUEST);
        }
        else{
            List<Follow> accountList = followRepository.findByAccount(account);

            Follow follow = accountList.stream()
                    .filter(a -> a.getTargetId().equals(targetId)).findFirst().get();
            followRepository.delete(follow);
            return new ResponseEntity("친구 삭제 성공",HttpStatus.OK);
        }
    }

    /**
     * targetId 존재 여부 기능
     */
    public boolean isPresentTargetId(Account account,String targetId){
        List<Follow> accountList = followRepository.findByAccount(account);

        boolean findByTargetId = accountList.stream() //targetId를 가진 follow객체
                .anyMatch(a -> a.getTargetId().equals(targetId));
        return findByTargetId;
    }

    public List<FollowResponseDto> listFollow(String myAccount, String targetId){
        List<Follow> targetFollow = followRepository.listMyId(targetId);
        List<FollowResponseDto> returnList = new ArrayList<>();
        for (Follow follow : targetFollow) {
            String targetUserName = accountRepository.getUserName(follow.getTargetId());
            boolean isCross = followRepository.getFollower(follow.getTargetId(), myAccount).isPresent();
            FollowResponseDto target = FollowResponseDto.builder().id(follow.getTargetId()).isCross(isCross).name(targetUserName).build();
            returnList.add(target);
        }
        return returnList;
    }

    public List<FollowResponseDto> listFollower(String myAccount){
        // 나를 팔로우 한사람 리스트
        List<Follow> targetFollow = followRepository.listFollower(myAccount);
        List<FollowResponseDto> returnList = new ArrayList<>();
        for (Follow follow : targetFollow) {
            String targetUserName = accountRepository.getUserName(follow.getAccount().getUserId());
            boolean isCross = followRepository.getFollower(myAccount, follow.getTargetId()).isPresent();
            FollowResponseDto target = FollowResponseDto.builder().id(follow.getAccount().getUserId()).isCross(isCross).name(targetUserName).build();
            returnList.add(target);
        }
        return returnList;
    }
}
