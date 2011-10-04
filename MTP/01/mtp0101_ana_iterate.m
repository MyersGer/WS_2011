function [x, y] = mtp0101_ana_iterate(h, xend)

    vec_xtmp = [];
    vec_ytmp = [];
    
    vec_range = [0:h:xend];
    for ska_cur_x=vec_range
        [foo, ska_y_i] = mtp0101_ana(ska_cur_x); 
        vec_xtmp = [vec_xtmp, ska_cur_x];
        vec_ytmp = [vec_ytmp, ska_y_i];
    end
    
    x = vec_xtmp;
    y = vec_ytmp;
    
end