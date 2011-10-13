function [x, y] = rk2(h, xend, f)
    ska_y_i = 1;
    vec_ytmp = [];
    vec_xtmp = [];
    
    vec_range = [0:h:xend];
    for ska_cur_x=vec_range
        k1 = f(ska_cur_x, ska_y_i);
        k2 = f(ska_cur_x + h, ska_y_i + h * k1);
        ska_y_i = ska_y_i + h/2 * (k1 + k2);
        vec_xtmp = [vec_xtmp, ska_cur_x];
        vec_ytmp = [vec_ytmp, ska_y_i];
    end
    
    y = vec_ytmp;
    x = vec_xtmp;
end