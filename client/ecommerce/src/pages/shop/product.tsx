import { useContext } from "react";
import { IProduct } from "../../models/interfaces";
import { IShopContext, ShopContext } from "../../context/shop-context";

interface Props {
  product: IProduct;
}
export const Product = (props: Props) => {
  const {
    productId,
    productName,
    description,
    price,
    productURL,
    sellerName,
    vocation,
    stockQuanity,
  } = props.product;
  const { addToCart, getCartItemCount } = useContext<IShopContext>(ShopContext);
  const cartItemCount: number = getCartItemCount(productId);
  // styling would be turn all of these into cards
  return (
    <>
      
      <div>
        <h3>{productName}</h3>
        <img src={productURL} />
        <p>{description}</p>
        <p>Cost: {price} Relm</p>
        <p>Speciality: {vocation}</p>
        <p>Seller: {sellerName}</p>
      </div>
      <div>
        {stockQuanity > 0 ? (
          <button onClick={() => addToCart(productId)}>
            Add To Satchel {cartItemCount > 0 && <>({cartItemCount})</>}
          </button>
        ) : (
          <h2>SOLD OUT</h2>
        )}
      </div>
    </>
  );
};