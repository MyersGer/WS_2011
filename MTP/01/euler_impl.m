function [x, y] = euler_expl(h, xend)
    ska_y_i = 1;
    vec_ytmp = [];
    vec_xtmp = [];
    
    vec_range = [0:h:xend];
    for ska_cur_x=vec_range
        ska_y_i = ska_y_i + h * mtp0101(ska_cur_x, ska_y_i); 
        vec_xtmp = [vec_xtmp, ska_cur_x];
        vec_ytmp = [vec_ytmp, ska_y_i];
    end
    
    y = vec_ytmp;
    x = vec_xtmp;
end