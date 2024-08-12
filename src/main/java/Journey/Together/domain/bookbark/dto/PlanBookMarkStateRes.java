package Journey.Together.domain.bookbark.dto;

public record PlanBookMarkStateRes(
        Boolean state
) {
    public static PlanBookMarkStateRes of (Boolean state){
        return new PlanBookMarkStateRes(state);
    }
}
