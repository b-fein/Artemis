module Exercise where

selectAndReflectA :: (Int,Int) -> [(Int,Int)] -> [(Int,Int)]
selectAndReflectA (i,j) xs = [(x,-y) | (x,y) <- xs, i <= x, x <= j]

selectAndReflectB :: (Int,Int) -> [(Int,Int)] -> [(Int,Int)]
selectAndReflectB (i,j) [] = []
selectAndReflectB (i,j) ((x,y):xs)
  | x >= i && x <= j = (x,-y) : selectAndReflectB (i,j) xs 
  | otherwise = selectAndReflectB (i,j) xs

selectAndReflectC :: (Int,Int) -> [(Int,Int)] -> [(Int,Int)]
selectAndReflectC (i,j) = map (\(x,y) -> (x,-y)) . filter (\(x,y) -> i <= x && x <= j)
