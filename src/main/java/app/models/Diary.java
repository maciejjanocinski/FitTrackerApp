package app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Diary {
    private NutrientsSum nutrientsSum;
    private List<UsersProductsEntity> products;
}
