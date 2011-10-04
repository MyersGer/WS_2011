function y = mtp0101_ana_fromXVec(vec_x)

    vec_xtmp = [];
    vec_ytmp = [];
    
    for ska_cur_x=vec_x
        [foo, ska_y_i] = mtp0101_ana(ska_cur_x); 
        vec_xtmp = [vec_xtmp, ska_cur_x];
        vec_ytmp = [vec_ytmp, ska_y_i];
    end
    
    y = vec_ytmp;
    
end