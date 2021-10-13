package test.database.dto;

public record UserFilter(int limit,
                         int offset,
                         String firstname,
                         String location) {

}
