function z = mtp0101(x, y)
    z = 10-500 * y + 5000 * x;
end

function [y, z] = mtp0101_ana(x)
    y = 10*x+ exp(-500);
    z = x;
end
