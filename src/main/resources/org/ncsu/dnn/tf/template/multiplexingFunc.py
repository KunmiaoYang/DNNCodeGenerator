

############## template code added for multiplexing ##############
# calculate the number of filter in a conv given config
selectdepth = lambda k,v: int(config[k]['ratio']*v) if config and k in config and 'ratio' in config[k] else v

# select the input tensor to a module
selectinput = lambda k, v: config[k]['input'] if config and k in config and 'input' in config[k] else v
############## end template code ##############
