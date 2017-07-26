import core

core.initialize(path="./graph_succeed1/aiGerms.meta", checkpoint="./graph_succeed1")
print("initialized")

weight = core.graph.get_tensor_by_name("weights_actor1:0")
print(core.sess.run(fetches=weight))

core.finalize(False)
print("finalize")
