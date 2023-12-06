from knext.core.builder.operator.model.op import BaseOp, ExtractOp, LinkOp, FuseOp, NormalizeOp, PromptOp


KnowledgeExtractOp = ExtractOp
EntityLinkOp = LinkOp
EntityFuseOp = FuseOp
PropertyNormalizeOp = NormalizeOp

__all__ = [
    "BaseOp",
    "ExtractOp",
    "LinkOp",
    "FuseOp",
    "NormalizeOp",
    "PromptOp",
    "LinkOp",
] + [
    "KnowledgeExtractOp",
    "EntityLinkOp",
    "EntityFuseOp",
    "PropertyNormalizeOp",
]
