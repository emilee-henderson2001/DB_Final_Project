package backend;

public class Member {
    private int MemberID;
    private String memberName;

    public Member(int memberID, String memberName) {
        this.MemberID = memberID;
        this.memberName = memberName;
    }
    public int getMemberID() {return MemberID;}
    public String getMemberName() {return memberName;}
}
