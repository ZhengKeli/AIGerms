import nerveCore

nerveCore.initialize(path="./graph_succeed1/aiGerms.meta", checkpoint="./graph_succeed1")
print("initialized")

weight = nerveCore.graph.get_tensor_by_name("weights_actor1:0")
print(nerveCore.sess.run(fetches=weight))

nerveCore.finalize(False)
print("finalize")
